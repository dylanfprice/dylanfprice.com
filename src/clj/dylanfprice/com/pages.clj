(ns dylanfprice.com.pages
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [dylanfprice.com.markdown :as dfp-md]
   [dylanfprice.com.meal-plans :refer [meal-plans]]))

(def recipes-dir "recipes")
(def quotes-dir "quotes")

(defn- header-link [attrs & body]
  [:a
   (merge {:class ["text-gray-600" "hover:text-blue-600"]} attrs)
   body])

(defn- page [title & content]
  [:html {:lang "en-US"}
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width"}]
    [:link {:rel "stylesheet" :href "/static/css/main.css"}]
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

(defn- page-data
  "Return map of page data for given markdown file."
  [file]
  (let [html-file (-> file (string/replace #".md$" "") (str "/index.html"))
        content (slurp file)]
    {:html-file html-file
     :file-name (-> file (string/split #"/") last (string/replace #".md$" ""))
     :link (str "/" (string/replace html-file #"/index.html$" ""))
     :hiccup (dfp-md/hiccup content)
     :metadata (dfp-md/metadata content)}))

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
               [:li (:main plan) (when side (list " with " (:side plan)))])]]))])

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

(defn- recipe-title [hiccup]
  ; [[:h1 {:id "title"} "Title"] ...]
  (-> hiccup first (nthrest 2)))

(defn- recipe-pages []
  (let [recipe-files (map #(str recipes-dir "/" %) (-> recipes-dir io/file .list sort))
        make-page (fn [{:keys [title hiccup]}] (page title hiccup))
        recipes (eduction
                 (map page-data)
                 (map #(assoc % :title (-> % :hiccup recipe-title)))
                 (map #(assoc % :hiccup (make-page %)))
                 recipe-files)
        recipe-pages (map (juxt :html-file :hiccup) recipes)]
    (conj recipe-pages (recipe-list-page recipes) (meal-plans-page))))

(defn- quote-list-page [quotes]
  (let [quotes-by-author (->> quotes
                              (group-by #(get-in % [:metadata :author]))
                              sort)]
    ["quotes/index.html"
     (page "Quotes"
           [:h1 "Quotes"]
           (for [[author quotes] quotes-by-author]
             (list
              [:h2 (-> author name)]
              [:ul (for [{:keys [link title]} quotes]
                     [:li [:a {:href link} title]])])))]))

(defn- quote-title [file-name]
  (-> file-name
      (string/capitalize)
      (string/replace "-" " ")
      (str "...")))

(defn- add-author [{:keys [metadata hiccup]}]
  (apply list
         (first hiccup)
         [:p.mt-4 "— " (:author metadata)]
         (rest hiccup)))

(defn- quote-pages []
  (let [quote-files (map #(str quotes-dir "/" %) (-> quotes-dir io/file .list sort))
        make-page (fn [{:keys [title hiccup]}] (page title hiccup))
        quotes (eduction
                (map page-data)
                (map #(assoc % :title (-> % :file-name quote-title)))
                (map #(assoc % :hiccup (add-author %)))
                (map #(assoc % :hiccup (make-page %)))
                quote-files)
        quote-pages (map (juxt :html-file :hiccup) quotes)]
    (conj quote-pages (quote-list-page quotes))))

(defn all-pages []
  (cons (home-page)
        (concat (recipe-pages) (quote-pages))))
