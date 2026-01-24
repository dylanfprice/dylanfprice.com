(ns dylanfprice.com.markdown-test
  (:require
   [clojure.string :as string]
   [clojure.test :refer :all]
   [dylanfprice.com.markdown :as test-ns]))

(deftest metadata
  (testing "returns metadata for markdown string whose first node is a clojure code block"
    (let [md (->> ["```clojure"
                   "{:key \"val\"}"
                   "```"
                   ""
                   "# Title"]
                  (string/join "\n"))]
      (is (= {:key "val"}
             (test-ns/metadata md)))))
  (testing "doesn't return metadata for markdown string with non clojure code block"
    (let [md (->> ["```"
                   "{:key \"val\"}"
                   "```"
                   ""
                   "# Title"]
                  (string/join "\n"))]
      (is (nil? (test-ns/metadata md)))))
  (testing "doesn't return metadata for markdown string with no code block"
    (let [md (->> ["# Title"]
                  (string/join "\n"))]
      (is (nil? (test-ns/metadata md))))))

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
