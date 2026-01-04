(ns dylanfprice.com.pages
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [dylanfprice.com.markdown :as dfp-md]))

(def recipes-dir "recipes")

(defn- header-link [attrs & body]
  [:a
   (merge {:class ["text-gray-600" "hover:text-blue-600"]} attrs)
   body])

(defn- page [title & content]
  [:html {:lang "en-US"}
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width"}]
    [:link {:rel "stylesheet" :href "/static/main.css"}]
    [:title title]]
   [:body {:class "p-2"}
    [:header {:class ["max-w-3xl" "mx-auto" "mb-2" "md:mb-10"
                      "font-light" "text-lg"
                      "grid" "grid-flow-row" "md:grid-flow-col"]}
     (header-link {:href "/"} "dylanfprice.com")
     [:nav {:class ["grid" "grid-flow-col" "gap-4" "justify-center" "md:justify-end"]}
      (header-link {:href "/recipes"} "Recipes")]]
    [:main {:class ["max-w-3xl" "mt-2" "mx-auto"]} content]]])

(defn- home []
  ["index.html" (page "Home" "")])

(defn- recipe [f]
  (let [html-file (-> f (string/replace #".md$" "") (str "/index.html"))]
    {:recipe-file f
     :html-file html-file
     :link (str "/" (string/replace html-file #"/index.html$" ""))
     :hiccup (page (dfp-md/title f) (dfp-md/hiccup f))}))

(defn- recipes []
  (let [recipe-files (map #(str recipes-dir "/" %) (-> recipes-dir  io/file .list sort))
        recipe-data (map recipe recipe-files)
        recipe-pages (map (juxt :html-file :hiccup) recipe-data)
        recipe-list ["recipes/index.html"
                     (page "Recipes"
                           [:h1 "Recipes"]
                           [:ul (for [{:keys [link recipe-file]} recipe-data]
                                  [:li [:a {:href link} (dfp-md/title recipe-file)]])])]]
    (concat [recipe-list] recipe-pages)))

(defn all-pages []
  (concat [(home)] (recipes)))
