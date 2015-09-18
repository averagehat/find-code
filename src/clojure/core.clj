(ns find-code.core
  (:require [clojure-questions.seqanswers :as h]
            [cheshire.core :as json]
            [clojure.string :as str]
[clj-http.client :as http]
  [ net.cgrand.enlive-html :as html]
            ))

;biostars relies on other proj
;(defn [q] biostars-answers
;  (get-questions q [:.result :a] "biostars.org"))
(defn [q] biostars-answers 
  (let [url (str "biostars.org/local/search/page/?q=" q)])
  (get-questions url [:.result :a] "biostars.org"))


(def q "https://www.biostars.org/local/search/page/?")
(http/get "https://api.stackexchange.com/2.2/search/advanced?" {:coerce :always
                                                                :accept "json"
                                                                :query-params 
                                                                {"q" "biostar" "site" "stackoverflow"}} )

(json/parse-string (:body r))

(defn so-raw [q]
  (http/get "https://api.stackexchange.com/2.2/search/advanced?" { :query-params 
                                                                {"q" q "site" "stackoverflow"}} ))

(def so-json (comp :items clojure.walk/keywordize-keys  json/parse-string :body so-raw))
;; don't really need to select keys
(def so-answers (comp (fn [j] (map #(select-keys % [:title :link :tags]) j)) so-json))

;; search multiple filetypes on google:
(def filetypes '(java py clj scala))

(def fts (str/join " OR " (map #(str "filetype:" %) filetypes)))
(def get-nodes (comp html/html-snippet :body http/get)) 
(def base (str "http://www.google.com/search?"))
(defn google-html [q] (get-nodes base {:query-params {"q" (str q " " fts)}}))
h
(def google-too-simple (comp (partial map extract-node) google-links))

(-> h (html/select [:h3.r :> :a html/first-child html/text])) ;first part of title? 
(-> h (html/select [:h3.r :> :a ]) ); contains attr->href
(-> h (html/select [:.st html/first-child html/text])) ;first part of snippet?
(-> h (html/select [:cite])  (#(->> % (map identity)) )) ;original link broken up by <br>

;(-> h (html/select [:h3.r :> :a ]) last :attrs :href (#(->> % (re-find #"q=([^&]+)") second))); contains attr->href

;; gets the original link
(-> h (html/select [:h3.r :> :a ]) 
  (#(->> %
    last :attrs :href 
    (re-find #"q=([^&]+)") second)))

(-> h (html/select [:h3.r :> :a ]) (#(map :content %)) flatten second  ) ;title
(-> h (html/select [:.st ])  (#(map :content %)) flatten second ) ;snippet

(defn google-links [q] 
  (-> q
    google-html
    (html/select [:li.g :h3.r :a]) ;this misses the body of the result
    flatten)) 

; would be nice to convert css to enlive selectors  or else use injected artoo.js
"
Stackoverflow spec


    q - a free form text parameter, will match all question properties based on an undocumented algorithm.
    accepted - true to return only questions with accepted answers, false to return only those without. Omit to elide constraint.
    answers - the minimum number of answers returned questions must have.
    body - text which must appear in returned questions' bodies.
    closed - true to return only closed questions, false to return only open ones. Omit to elide constraint.
    migrated - true to return only questions migrated away from a site, false to return only those not. Omit to elide constraint.
    notice - true to return only questions with post notices, false to return only those without. Omit to elide constraint.
    nottagged - a semicolon delimited list of tags, none of which will be present on returned questions.
    tagged - a semicolon delimited list of tags, of which at least one will be present on all returned questions.
    title - text which must appear in returned questions' titles.
    user - the id of the user who must own the questions returned.
    url - a url which must be contained in a post, may include a wildcard.
    views - the minimum number of views returned questions must have.
    wiki - true to return only community wiki questions, false to return only non-community wiki ones. Omit to elide constraint.
"
