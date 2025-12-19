(ns dylanfprice.com.pages
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [nextjournal.markdown :as md]))

(def recipes-dir "recipes")

(defn page [& content]
  [:html
   [:head
    [:link {:rel "stylesheet" :href "/static/main.css"}]]
   [:body
    (let [header-classes ["text-gray-600" "hover:text-blue-600"]]
      [:header {:class ["max-w-3xl" "mx-auto" "mb-10" "font-light" "text-lg" "grid" "grid-flow-col"]}
       [:a {:class header-classes :href "/"} "dylanfprice.com"]
       [:nav {:class ["grid" "grid-flow-col" "gap-4" "justify-end"]}
        [:a {:class header-classes :href "/"} "Home"]
        [:a {:class header-classes :href "/recipes"} "Recipes"]]])
    [:main {:class ["max-w-3xl" "mt-2" "mx-auto"]} content]]])

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
                     (page
                      [:h1 "Recipes"]
                      [:ul (for [[f _hiccup] recipe-pages
                                 :let [title (-> f
                                                 io/file
                                                 .getName
                                                 (string/replace #".html$" "")
                                                 (string/replace "-" " ")
                                                 (string/capitalize))]]
                             [:li [:a {:href (str "/" f)} title]])])]]
    (concat [recipe-list] recipe-pages)))

(defn all-pages []
  (concat [(home)] (recipes)))
