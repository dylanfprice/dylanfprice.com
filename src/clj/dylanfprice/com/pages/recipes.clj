(ns dylanfprice.com.pages.recipes
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [dylanfprice.com.meal-plans :refer [meal-plans]]
   [dylanfprice.com.pages.page :refer [html page page-data]]))

(def recipes-dir "recipes")

(defn- meal-plans-page []
  ["recipes/meal-plans/index.html"
   (html
    (page (list [:title "Meal Plans"])
          [:h1 "Meal Plans"]
          (for [[heading plans] meal-plans]
            [:div
             [:h2 heading]
             [:ul
              (for [plan plans
                    :let [side (:side plan)]]
                [:li (:main plan) (when side (list " with " (:side plan)))])]])))])

(defn- recipe-list-page [recipes]
  (let [recipes-by-category (group-by #(get-in % [:metadata :category]) recipes)]
    ["recipes/index.html"
     (html
      (page (list [:title "Recipes"])
            [:h1 "Recipes"]
            [:a {:href "/recipes/meal-plans"} "Meal Plans"]
            (for [[category recipes] recipes-by-category]
              (list
               [:h2 (-> category name string/capitalize)]
               [:ul (for [{:keys [link title]} recipes]
                      [:li [:a {:href link} title]])]))))]))

(defn- recipe-title [hiccup]
  ; [[:h1 {:id "title"} "Title"] ...]
  (-> hiccup first (nthrest 2)))

(defn recipe-pages []
  (let [recipe-files (map #(str recipes-dir "/" %) (-> recipes-dir io/file .list sort))
        make-page (fn [{:keys [title hiccup]}] (page (list [:title title]) hiccup))
        recipes (eduction
                 (map page-data)
                 (map #(assoc % :title (-> % :hiccup recipe-title)))
                 (map #(assoc % :hiccup (make-page %)))
                 (map #(assoc % :html (-> % :hiccup html)))
                 recipe-files)
        recipe-pages (map (juxt :dest :html) recipes)]
    (conj recipe-pages (recipe-list-page recipes) (meal-plans-page))))
