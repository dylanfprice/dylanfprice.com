(ns dylanfprice.com.pages.quote-of-the-day
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [dylanfprice.com.pages.page :refer [html page page-data]]
   [hiccup2.core :as h]))

(def quotes-dir "quotes")

(defn- quote-of-the-day-page [quotes]
  ["quote-of-the-day/index.html"
   (html
    (page (list [:title "Loading..."]
                [:script {:type "application/javascript"
                          :defer true
                          :src "/js/scittle-0.8.31.js"}]
                [:script {:type "application/x-scittle"
                          :defer true}
                 (->> ["(ns dylanfprice.com.quotes)"
                       (str "(def quotes " (mapv :link quotes) ")")]
                      (string/join "\n")
                      h/raw)]
                [:script {:type "application/x-scittle"
                          :defer true
                          :src "/cljs/dylanfprice/com/quote_of_the_day.cljs"}])
          [:div {:role "status", :class ["animate-pulse" "pt-1"]}
           [:div {:class ["h-3" "max-w-[640px]" "bg-gray-200" "rounded-full" "mb-3"]}]
           [:div {:class ["h-3" "max-w-[640px]" "bg-gray-200" "rounded-full" "mb-6"]}]
           [:div {:class ["h-3" "max-w-[100px]" "bg-gray-200" "rounded-full"]}]
           [:span {:class "sr-only"} "Loading..."]]))])

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

(defn quote-pages []
  (let [quote-files (map #(str quotes-dir "/" %) (-> quotes-dir io/file .list sort))
        make-page (fn [{:keys [title hiccup]}] (page (list [:title title]) hiccup))
        quotes (eduction
                (map page-data)
                (map #(assoc % :title (-> % :file-name quote-title)))
                (map #(assoc % :hiccup (add-author %)))
                (map #(assoc % :hiccup (make-page %)))
                (map #(assoc % :html (-> % :hiccup html)))
                quote-files)
        quote-pages (map (juxt :dest :html) quotes)]
    (conj quote-pages (quote-of-the-day-page quotes))))
