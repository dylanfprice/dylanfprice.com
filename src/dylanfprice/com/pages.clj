(ns dylanfprice.com.pages
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [nextjournal.markdown :as md]))

(comment
  (require '[clojure.repl :as repl])
  (repl/doc md/into-hiccup)
  md/default-hiccup-renderers)

(def recipes-dir "recipes")

(defn header-link [attrs & body]
  [:a
   (merge {:class ["text-gray-600" "hover:text-blue-600"]}
          attrs)
   body])

(defn page [title & content]
  [:html {:lang "en-US"}
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width"}]
    [:link {:rel "stylesheet" :href "/static/main.css"}]
    [:title title]]
   [:body {:class "p-2"}
    [:header {:class ["max-w-3xl" "mx-auto" "mb-10"
                      "font-light" "text-lg"
                      "grid" "grid-flow-row" "md:grid-flow-col"]}
     (header-link {:href "/"} "dylanfprice.com")
     [:nav {:class ["grid" "grid-flow-col" "gap-4" "justify-center" "md:justify-end"]}
      (header-link {:href "/recipes"} "Recipes")]]
    [:main {:class ["max-w-3xl" "mt-2" "mx-auto"]} content]]])

(defn home []
  ["index.html" (page "Home" "")])

(defn ast->hiccup [ast]
  (md/->hiccup
   (assoc md/default-hiccup-renderers
          :footnote-ref (fn [_ctx {:keys [label ref]}]
                          [:sup [:a
                                 {:id (str "reference-" ref)
                                  :href (str "#footnote-" ref)}
                                 label]]))
   ast))

(defn ast-footnotes->hiccup [ast]
  (let [renderers (assoc md/default-hiccup-renderers
                         :footnote (fn [_ctx {:keys [label ref] :as node}]
                                     [:p {:id (str "footnote-" ref)}
                                      (str "[" label "]: ")
                                      (md/node->text node)
                                      [:a {:href (str "#reference-" ref)} "↩"]]))]
    (for [footnote (:footnotes ast)]
      (md/->hiccup renderers footnote))))

(defn title [f]
  (-> f
      io/file
      .getName
      (string/replace #"\.(html|md)$" "")
      (string/replace "-" " ")
      (string/capitalize)))

(defn recipe [f]
  (let [new-file (string/replace f #".md$" ".html")
        ast (-> f slurp md/parse)
        hiccup (list (ast->hiccup ast)
                     [:section {:class ["mt-8"]} (ast-footnotes->hiccup ast)])]
    [new-file (page (title f) hiccup)]))

(defn recipes []
  (let [recipe-files (map #(str recipes-dir "/" %) (-> recipes-dir  io/file .list sort))
        recipe-pages (map recipe recipe-files)
        recipe-list ["recipes/index.html"
                     (page "Recipes"
                           [:h1 "Recipes"]
                           [:ul (for [[f _hiccup] recipe-pages]
                                  [:li [:a {:href (str "/" f)} (title f)]])])]]
    (concat [recipe-list] recipe-pages)))

(defn all-pages []
  (concat [(home)] (recipes)))
