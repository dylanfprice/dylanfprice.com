(ns dylanfprice.com.markdown
  (:require
   [clojure.edn :as edn]
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

(defn- metadata-node [ast]
  (let [first-node (-> ast :content first)]
    (when (and
           (= :code (:type first-node))
           (= "clojure" (:info first-node)))
      first-node)))

(defn metadata [markdown-string]
  (let [ast (md/parse markdown-string)]
    (when-let [node (metadata-node ast)]
      (-> node :content first :text edn/read-string))))

(defn hiccup [markdown-string]
  (let [ast (md/parse markdown-string)
        ast (if (metadata-node ast)
              (assoc ast :content (-> ast :content rest vec))
              ast)]
    (concat (-> ast ast->hiccup rest)
            (list [:section {:class ["mt-8"]} (ast-footnotes->hiccup ast)]))))
