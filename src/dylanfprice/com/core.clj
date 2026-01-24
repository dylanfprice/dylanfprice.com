(ns dylanfprice.com.core
  (:require
   [babashka.http-server :as http-server]
   [clojure.java.io :as io]
   [clojure.java.process :as process]
   [dylanfprice.com.pages :as pages]
   [hiccup2.core :as h]
   [kaocha.runner]))

(def public-dir "public")

(defn terraform [args]
  (let [get-caller-identity (process/start
                             {:out :discard :err :discard}
                             "aws" "--profile=dfp" "sts" "get-caller-identity")
        logged-in (= 0 @(process/exit-ref get-caller-identity))]
    (when (not logged-in)
      (process/exec {:out :inherit :err :inherit}
                    "aws" "--profile=dfp" "sso" "login")))
  (let [terraform (apply process/start
                         {:in :inherit :out :inherit :err :inherit}
                         "terraform" args)
        exit-code @(process/exit-ref terraform)]
    (System/exit exit-code)))

(defn build []
  (process/exec "rm" "-rf" public-dir)
  (run!
   (fn [[path, hiccup]]
     (let [path (str "public/" path)
           html (str (h/html {:mode :html} (h/raw "<!DOCTYPE html>") hiccup))]
       (.mkdirs (-> path io/file .getParentFile))
       (spit path html)))
   (pages/all-pages))
  (process/exec {:err :pipe}
                "tailwindcss"
                "--optimize"
                "-i" "src/main.css"
                "-o" "public/static/main.css"))

(defn run-tests [args]
  (apply kaocha.runner/-main args))

(defn serve [{:keys [port]}]
  (http-server/serve {:dir public-dir :port port}))

(defn publish []
  (process/exec "tar"
                "--directory=public"
                "--file=site.tar.gz"
                "--create" "--gzip"
                ".")
  (process/exec "hut" "pages" "publish"
                "--domain=dylanfprice.com"
                "site.tar.gz"))
