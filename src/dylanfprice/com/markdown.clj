(ns dylanfprice.com.markdown
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [nextjournal.markdown :as md]))

(defn- ast->hiccup [ast]
  (md/->hiccup
   (assoc md/default-hiccup-renderers
          :footnote-ref (fn [_ctx {:keys [label ref]}]
                          [:sup [:a
                                 {:id (str "reference-" ref)
                                  :href (str "#footnote-" ref)}
                                 label]]))
   ast))

(defn- ast-footnotes->hiccup [ast]
  (let [renderers (assoc md/default-hiccup-renderers
                         :footnote (fn [_ctx {:keys [label ref] :as node}]
                                     [:p {:id (str "footnote-" ref)}
                                      (str "[" label "]: ")
                                      (md/node->text node)
                                      [:a {:href (str "#reference-" ref)} "↩"]]))]
    (for [footnote (:footnotes ast)]
      (md/->hiccup renderers footnote))))

(defn hiccup [f]
  (let [ast (-> f slurp md/parse)]
    (list (ast->hiccup ast)
          [:section {:class ["mt-8"]} (ast-footnotes->hiccup ast)])))

(defn title [f]
  (-> f
      io/file
      .getName
      (string/replace #"\.(md)$" "")
      (string/replace "-" " ")
      (string/capitalize)))

