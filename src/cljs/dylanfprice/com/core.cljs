(ns dylanfprice.com.core
  (:require [clojure.edn :as edn]))

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

(defn goto-link-of-the-day [links]
  (let [link (pick-item-of-the-day links)]
    (js/window.location.replace link)))

(defn fetch-edn [url handler]
  (-> (js/fetch url)
      (.then (fn [resp]
               (-> (.text resp)
                   (.then (fn [text]
                            (handler (edn/read-string text)))))))
      (.catch js/console.log)))

(fetch-edn "/quotes/list.edn" goto-link-of-the-day)
