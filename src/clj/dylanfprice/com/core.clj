(ns dylanfprice.com.core
  (:require
   [babashka.http-server :as http-server]
   [clojure.java.io :as io]
   [clojure.java.process :as process]
   [dylanfprice.com.pages.core :as pages]
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
  (process/exec "mkdir" public-dir)
  (process/exec "cp" "-r" "src/js" "public/js")
  (process/exec "cp" "-r" "src/cljs" "public/cljs")
  (process/exec "cp" "-r" "src/svg" "public/svg")
  (process/exec {:err :pipe}
                "tailwindcss"
                "--optimize"
                "-i" "src/css/main.css"
                "-o" "public/css/main.css")
  (run!
   (fn [[dest, contents]]
     (let [path (str "public/" dest)]
       (.mkdirs (-> path io/file .getParentFile))
       (spit path contents)))
   (pages/all-pages)))

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
                "--site-config=site-config.json"
                "site.tar.gz"))
