(ns dylanfprice.com.pages.weather
  (:require
   [dylanfprice.com.pages.page :refer [html page]]))

(defn- weather-page []
  ["weather/index.html"
   (html
    (page (list [:title "Weather"]
                [:script {:type "application/javascript"
                          :defer true
                          :src "/js/scittle-0.8.31.js"}]
                [:script {:type "application/x-scittle"
                          :defer true
                          :src "/cljs/dylanfprice/com/weather.cljs"}])
          [:h1 "Weather"]
          [:div {:class ["grid" "grid-flow-col" "gap-4" "w-xs"]}
           [:input#location-input {:type "text"
                                   :required true
                                   :placeholder "lat, lng"
                                   :pattern (str #"-?\d{1,3}\.?\d*,\s*-?\d{1,3}\.?\d*")
                                   :class ["col-span-2" "peer"
                                           "border" "border-black"
                                           "user-invalid:border-2"
                                           "user-invalid:border-red-500"]}]
           [:input#load-weather {:type "submit"
                                 :value "Go"
                                 :class ["col-span-1" "rounded-full"
                                         "text-gray-300" "bg-gray-200"
                                         "peer-valid:text-black"
                                         "peer-valid:hover:bg-gray-300"
                                         "peer-valid:hover:cursor-pointer"]}]]
          [:p#location]
          [:p#elevation]
          [:section#forecast.hidden
           [:h2 "Forecast"]]
          [:section#discussion.hidden
           [:h2 "Discussion"]]))])

(defn weather-pages []
  [(weather-page)])

