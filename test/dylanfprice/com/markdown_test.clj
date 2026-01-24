(ns dylanfprice.com.markdown-test
  (:require
   [clojure.string :as string]
   [clojure.test :refer :all]
   [dylanfprice.com.markdown :as test-ns]))

(deftest hiccup
  (testing "return hiccup for simple markdown string"
    (let [md (->> ["# Title"
                   ""
                   "Some content"]
                  (string/join "\n"))]
      (is (= '([:h1 {:id "title"} "Title"]
               [:p "Some content"]
               [:section {:class ["mt-8"]} []])
             (test-ns/hiccup md)))))
  (testing "return footnotes for markdown with footnotes"
    (let [md (->> ["# Title"
                   ""
                   "Some content[^1]"
                   ""
                   "[^1]: A footnote!"]
                  (string/join "\n"))]
      (is (= '([:h1 {:id "title"} "Title"]
               [:p
                "Some content"
                [:sup [:a {:id "reference-0", :href "#footnote-0"} "1"]]]
               [:section
                {:class ["mt-8"]}
                ([:p
                  {:id "footnote-0"}
                  "[1]: "
                  "A footnote!"
                  [:a {:href "#reference-0"} "↩"]])])
             (test-ns/hiccup md))))))
