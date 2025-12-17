(ns dylanfprice.com.pages
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [nextjournal.markdown :as md]))

(def recipes-dir "recipes")

(defn page [content]
  [:html
   [:body
    [:header
     [:nav [:ul
            [:li [:a {:href "/"} "Home"]]
            [:li [:a {:href "/recipes"} "Recipes"]]]]]
    [:main content]]])

(defn home []
  ["index.html" (page "")])

(defn recipe [f]
  (let [new-file (string/replace f #".md$" ".html")
        hiccup (-> f slurp md/parse md/->hiccup)]
    [new-file (page hiccup)]))

(defn recipes []
  (let [recipe-files (map #(str recipes-dir "/" %) (.list (io/file recipes-dir)))
        recipe-pages (map recipe recipe-files)
        recipe-list ["recipes/index.html"
                     [:ul (for [[f _hiccup] recipe-pages
                                :let [title (-> f
                                                io/file
                                                .getName
                                                (string/replace #".html$" "")
                                                (string/replace "-" " ")
                                                (string/capitalize))]]
                            [:li [:a {:href (str "/" f)} title]])]]]
    (concat [recipe-list] recipe-pages)))

(defn all-pages []
  (concat [(home)] (recipes)))
