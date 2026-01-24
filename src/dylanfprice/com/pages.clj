(ns dylanfprice.com.pages
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [dylanfprice.com.markdown :as dfp-md]
   [dylanfprice.com.meal-plans :refer [meal-plans]]))

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

(defn- home-page []
  ["index.html" (page "Home" "")])

(defn- recipe [f]
  (let [html-file (-> f (string/replace #".md$" "") (str "/index.html"))
        content (slurp f)
        hiccup (dfp-md/hiccup content)
              ; [[:h1 {:id "title"} "Title"] ...]
        title (-> hiccup first (nthrest 2))]
    {:recipe-file f
     :html-file html-file
     :link (str "/" (string/replace html-file #"/index.html$" ""))
     :title title
     :metadata (dfp-md/metadata content)
     :hiccup (page title hiccup)}))

(defn- meal-plans-page []
  ["recipes/meal-plans/index.html"
   (page "Meal Plans"
         [:h1 "Meal Plans"]
         (for [[heading plans] meal-plans]
           [:div
            [:h2 heading]
            [:ul
             (for [plan plans
                   :let [side (:side plan)]]
               [:li (:entree plan) (when side (list " with " (:side plan)))])]]))])

(defn- recipe-list-page [recipes]
  (let [recipes-by-category (group-by #(get-in % [:metadata :category]) recipes)]
    ["recipes/index.html"
     (page "Recipes"
           [:h1 "Recipes"]
           [:a {:href "/recipes/meal-plans"} "Meal Plans"]
           (for [[category recipes] recipes-by-category]
             (list
              [:h2 (-> category name string/capitalize)]
              [:ul (for [{:keys [link title]} recipes]
                     [:li [:a {:href link} title]])])))]))

(defn- recipe-pages []
  (let [recipe-files (map #(str recipes-dir "/" %) (-> recipes-dir io/file .list sort))
        recipes (map recipe recipe-files)
        recipe-pages (map (juxt :html-file :hiccup) recipes)]
    (conj recipe-pages (recipe-list-page recipes) (meal-plans-page))))

(defn all-pages []
  (conj (recipe-pages) (home-page)))
