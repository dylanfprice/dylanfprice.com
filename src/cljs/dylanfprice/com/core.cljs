(ns dylanfprice.com.core
  (:require [dylanfprice.com.quotes :refer [quotes]]))

(def one-day-ms
  (let [hours-per-day 24
        min-per-hour 60
        sec-per-min 60
        ms-per-sec 1000]
    (* 1 hours-per-day min-per-hour sec-per-min ms-per-sec)))

(defn pick-item-of-the-day [items]
  (let [time (.getTime (js/Date.))
        day (/ time one-day-ms)
        idx (mod day (count items))]
    (nth items idx)))

(defn fetch-text
  "Returns js promise."
  [url]
  (-> (js/fetch url)
      (.then #(.text %))
      (.catch js/console.log)))

(defn replace-document [html]
  (js/document.open)
  (js/document.write html)
  (js/document.close))

(defn render-link-of-the-day [links]
  (let [link (pick-item-of-the-day links)]
    (-> (fetch-text link)
        (.then replace-document))))

(render-link-of-the-day quotes)
