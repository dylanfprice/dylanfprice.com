(ns dylanfprice.com.weather
  (:require [clojure.string :as string]))

(def ft-per-m 3.2808)
(def base-endpoint "https://api.weather.gov")

(defn- ^:async fetch-json [uri]
  (let [resp (await (js/fetch
                     uri
                     (clj->js {:headers {:accept "application/ld+json,application/geo+json"}})))
        json (await (.json resp))]
    (js->clj json :keywordize-keys true)))

(defn- ^:async fetch-points [location]
  (await (fetch-json (str base-endpoint "/points/" location))))

(defn- ^:async fetch-forecast [points]
  (await (-> points (get-in [:properties :forecast]) fetch-json)))

(defn- ^:async fetch-discussion-text [points]
  (let [office-id (get-in points [:properties :gridId])
        discussion (await (fetch-json (str base-endpoint
                                           "/products/types/AFD/locations/"
                                           office-id
                                           "/latest")))]
    (:productText discussion)))

(defn- forecast-elevation-ft [forecast]
  (let [{{{unit-code :unitCode value :value} :elevation} :properties} forecast]
    (case unit-code
      "wmoUnit:m" (Math/round (* value ft-per-m)))))

(defn- forecast-texts [forecast]
  (map (fn [period]
         (str (:name period) ": " (:detailedForecast period)))
       (get-in forecast [:properties :periods])))

(defn- get-element [id]
  (js/document.getElementById id))

(defn- create-element
  ([tag] (js/document.createElement tag))
  ([tag text]
   (let [element (create-element tag)]
     (set! (.-textContent element) text)
     element)))

(defn- clear-children [element]
  (while (.-firstChild element)
    (.removeChild element (.-lastChild element))))

(defn- clear-all-but-first-child [element]
  (while (> (.. element -childNodes -length) 1)
    (.removeChild element (.-lastChild element))))

(defn- append-text [element text]
  (let [text-node (js/document.createTextNode text)]
    (.appendChild element text-node)))

(defn- update-forecast [points forecast]
  (let [location (string/join ", " (reverse (get-in points [:geometry :coordinates])))
        elevation-ft (forecast-elevation-ft forecast)
        location-element (get-element "location")
        elevation-element (get-element "elevation")
        forecast-element (get-element "forecast")]
    (clear-children location-element)
    (clear-children elevation-element)
    (append-text location-element (str "Location: " location))
    (append-text elevation-element (str "Elevation: " elevation-ft " ft"))
    (.remove (.-classList forecast-element) "hidden")
    (clear-all-but-first-child forecast-element)
    (doseq [text (forecast-texts forecast)
            :let [element (create-element "p" text)]]
      (.setAttribute element "class" "mt-2")
      (.appendChild forecast-element element))))

(defn- update-discussion [discussion-text]
  (let [discussion-lines (-> discussion-text
                             (string/replace-first #"(?s).*(SYNOPSIS|KEY MESSAGES)" ".$1")
                             (string/split "&&"))
        discussion-lines (eduction
                          (map string/trim)
                          (remove #(re-find #"^\$.*" %))
                          (map #(subs % 1))
                          discussion-lines)
        discussion-element (get-element "discussion")]
    (.remove (.-classList discussion-element) "hidden")
    (clear-all-but-first-child discussion-element)
    (doseq [line discussion-lines
            :let [element (create-element "p" line)]]
      (.setAttribute element "class" "mt-2")
      (.appendChild discussion-element element))))

(defn- show-loading-spinner [show]
  (let [spinner-element (get-element "load-weather-spinner")
        button-element (get-element "load-weather")]
    (if show
      (do (.remove (.-classList spinner-element) "hidden")
          (.add (.-classList button-element) "hidden"))
      (do (.add (.-classList spinner-element) "hidden")
          (.remove (.-classList button-element) "hidden")))))

(defn- ^:async load-weather
  "Load weather from a \"lat,lng\" string."
  [location]
  (show-loading-spinner true)
  (try
    (let [points (await (fetch-points location))
          forecast (await (fetch-forecast points))
          discussion-text (await (fetch-discussion-text points))]
      (update-forecast points forecast)
      (update-discussion discussion-text))
    (catch js/Error e
      (append-text (get-element "location") (str "Error: " e)))
    (finally
      (show-loading-spinner false))))

(defn- click-handler []
  (let [input-element (get-element "location-input")
        location (.-value input-element)]
    (load-weather location)))

(defn- geolocate []
  (.. js/navigator
      -geolocation
      (getCurrentPosition
       (fn [position]
         (let [location (str (.. position -coords -latitude)
                             ","
                             (.. position -coords -longitude))]
           (load-weather location))))))

(defn- main []
  (.addEventListener (get-element "location-input")
                     "keyup"
                     (fn [e] (when (= (.-key e) "Enter") (click-handler))))

  (.addEventListener (get-element "load-weather")
                     "click" click-handler)

  (.addEventListener (get-element "geolocate")
                     "click" geolocate))

(main)
