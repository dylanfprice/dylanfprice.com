(ns dylanfprice.com.core
  (:require
   [babashka.http-server :as http-server]
   [clojure.java.io :as io]
   [clojure.java.process :as process]
   [clojure.string :as string]
   [nextjournal.markdown :as md]
   [hiccup2.core :as h]))

(def public-dir "public")
(def recipes-dir "recipes")

(defn index-page []
  ["index.html"
   [:html
    [:body
     [:nav [:ul
            [:li [:a {:href "/"} "Home"]]
            [:li [:a {:href "/recipes"} "Recipes"]]]]]]])

(defn recipe-page [f]
  (let [new-name (string/replace f #".md$" ".html")
        hiccup (-> f slurp md/parse md/->hiccup)]
    [new-name hiccup]))

(defn build []
  (.mkdirs (io/file public-dir "recipes"))
  (let [recipe-pages (eduction
                      (map #(str recipes-dir "/" %))
                      (map recipe-page)
                      (.list (io/file recipes-dir)))
        pages (concat [(index-page)]
                      recipe-pages)]
    (run!
     (fn [[path, hiccup]] (spit (str "public/" path)
                                (str (h/html hiccup))))
     pages)))

(defn terraform [args]
  (let [get-caller-identity (process/start
                             {:out :discard :err :discard}
                             "aws" "--profile=dfp" "sts" "get-caller-identity")
        logged-in (= 0 @(process/exit-ref get-caller-identity))]
    (when (not logged-in)
      (process/exec {:out :inherit :err :inherit}
                    "aws" "--profile=dfp" "sso" "login")))
  (let [terraform (apply process/start
                         {:in :inherit :out :inherit :err :inherit}
                         "terraform" args)
        exit-code @(process/exit-ref terraform)]
    (System/exit exit-code)))

(defn serve [{:keys [port]}]
  (http-server/serve {:dir public-dir :port port}))
