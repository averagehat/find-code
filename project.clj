(defproject clojure-questions "0.1.0"
  :description "A little Twitter bot that tweets Clojure stackoverflow questions."
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [reaver "0.1.1"]
                 [clj-http "0.7.8"]
                 [cheshire "5.3.0"]
                 [environ "0.4.0"]
                 [twitter-api "0.7.5"]
                 [clj-tagsoup/clj-tagsoup "0.3.0"]
                 [enlive "1.1.6"]

                                  [clj-http "0.7.8"]
                                  [hickory "0.5.2"]
                                  [org.clojure/data.json "0.2.4"]
                 [org.apache.commons/commons-lang3 "3.2.1"]]
  :plugins [[lein-environ "0.4.0"]]
  :main clojure-questions.core)
