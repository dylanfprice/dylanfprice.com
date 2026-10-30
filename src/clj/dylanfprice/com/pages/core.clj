(ns dylanfprice.com.pages.core
  (:require
   [dylanfprice.com.pages.page :refer [html page]]
   [dylanfprice.com.pages.recipes :refer [recipe-pages]]
   [dylanfprice.com.pages.weather :refer [weather-pages]]))

(defn- home-page []
  ["index.html" (html (page (list [:title "Home"]) ""))])

(defn all-pages []
  (cons (home-page)
        (concat (recipe-pages) (weather-pages))))
