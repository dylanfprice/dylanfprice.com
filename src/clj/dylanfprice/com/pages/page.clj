(ns dylanfprice.com.pages.page
  (:require
   [clojure.string :as string]
   [dylanfprice.com.markdown :as dfp-md]
   [hiccup2.core :as h]))

(defn- header-link [attrs & body]
  [:a
   (merge {:class ["text-gray-600" "hover:text-blue-600"]} attrs)
   body])

(defn page [head & content]
  [:html {:lang "en-US"}
   [:head
    (apply list
           [:meta {:charset "utf-8"}]
           [:meta {:name "viewport" :content "width=device-width"}]
           [:link {:rel "stylesheet" :href "/css/main.css"}]
           head)]
   [:body {:class "p-2"}
    [:header {:class ["max-w-3xl" "mx-auto" "mb-2" "md:mb-10"
                      "font-light" "text-lg"
                      "grid" "grid-flow-row" "md:grid-flow-col"]}
     (header-link {:href "/"} "dylanfprice.com")
     [:nav {:class ["grid" "grid-flow-col" "gap-4" "justify-center" "md:justify-end"]}
      (header-link {:href "/quote-of-the-day/"} "Quote")
      (header-link {:href "/recipes/"} "Recipes")]]
    [:main {:class ["max-w-3xl" "mt-2" "mx-auto"]} content]]])

(defn html [hiccup]
  (str (h/html {:mode :html} (h/raw "<!DOCTYPE html>") hiccup)))

(defn page-data
  "Return map of page data for given markdown file."
  [file]
  (let [html-file (-> file (string/replace #".md$" "") (str "/index.html"))
        content (slurp file)]
    {:file-name (-> file (string/split #"/") last (string/replace #".md$" ""))
     :dest html-file
     :link (str "/" (string/replace html-file #"index.html$" ""))
     :hiccup (dfp-md/hiccup content)
     :metadata (dfp-md/metadata content)}))
