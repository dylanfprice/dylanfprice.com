(ns dylanfprice.com.pages.weather
  (:require
   [dylanfprice.com.pages.page :refer [html page]]))

(defn- spinner [id classes]
  [:svg {:id id
         :class (concat classes ["size-5" "text-black" "animate-spin" "m-auto"])
         :viewBox "0 0 24 24"
         :fill "none"}
   [:circle {:class "opacity-25"
             :cx 12
             :cy 12
             :r 10
             :stroke "currentColor"
             :strokeWidth 4}]
   [:path {:class "opacity-75"
           :fill "currentColor"
           :d "M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"}]])

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
                                   :class ["col-span-3" "peer"
                                           "border" "border-black"
                                           "user-invalid:border-2"
                                           "user-invalid:border-red-500"]}]
           [:input#load-weather {:type "submit"
                                 :value "Go"
                                 :class ["col-span-2" "rounded-full"
                                         "text-gray-300" "bg-gray-200"
                                         "peer-valid:text-black"
                                         "peer-valid:hover:bg-gray-300"
                                         "peer-valid:hover:cursor-pointer"]}]
           (spinner "load-weather-spinner" ["hidden"])
           [:input#geolocate {:type "image"
                              :src "/svg/location.svg"
                              :height 25
                              :class ["col-span-1" "text-black"]}]]
          [:p#location]
          [:p#elevation]
          [:section#forecast.hidden
           [:h2 "Forecast"]]
          [:section#discussion.hidden
           [:h2 "Discussion"]]))])

(defn weather-pages []
  [(weather-page)])

