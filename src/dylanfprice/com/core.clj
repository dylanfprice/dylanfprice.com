(ns dylanfprice.com.core
  (:require
   [babashka.http-server :as http-server]
   [clojure.java.process :as process]))

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

(defn serve [{:keys [port]}]
  (http-server/serve {:dir public-dir :port port}))
