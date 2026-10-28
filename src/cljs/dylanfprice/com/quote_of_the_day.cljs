(ns dylanfprice.com.quote-of-the-day
  (:require [dylanfprice.com.quotes :refer [quotes]]))

(def hours-per-day 24)
(def min-per-hour 60)
(def sec-per-min 60)
(def ms-per-sec 1000)
(def day-ms
  (* 1 hours-per-day min-per-hour sec-per-min ms-per-sec))
(def hour-ms
  (* 1 min-per-hour sec-per-min ms-per-sec))

(defn pdt-time []
  (let [time (.getTime (js/Date.))
        pdt-offset (* -7 hour-ms)]
    (+ time pdt-offset)))

(defn pick-item-of-the-day [items]
  (let [time (pdt-time)
        day (/ time day-ms)
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
