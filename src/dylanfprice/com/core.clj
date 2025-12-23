(ns dylanfprice.com.core
  (:require
   [babashka.http-server :as http-server]
   [clojure.java.io :as io]
   [clojure.java.process :as process]
   [hiccup2.core :as h]
   [dylanfprice.com.pages :as pages]))

(def public-dir "public")

(defn build []
  (.mkdirs (io/file public-dir "recipes"))
  (run!
   (fn [[path, hiccup]] (spit (str "public/" path)
                              (str (h/html hiccup))))
   (pages/all-pages))
  (process/exec {:err :pipe}
                "tailwindcss"
                "--optimize"
                "-i" "src/main.css"
                "-o" "public/static/main.css"))

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
