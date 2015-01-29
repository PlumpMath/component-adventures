(ns no-component
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clojure.core.async :as async :refer [go chan pub put! <!]])
  (:gen-class))                         ;:gen-class here means this
                                        ;namespace will be compiled
                                        ;into a .class file if the aot
                                        ;task is run

(defn -main [& args]                    ;This -main function will
                                        ;become the main method if we
                                        ;set up this namespace to aot,
                                        ;and point jar -m at it
  (println "Hello world!"))


(defn make-spreadsheet-url [id]
  "given a spreadsheet url, returns google endpoint that has
   spreadsheet values in json"
  (str "https://spreadsheets.google.com/feeds/list/" 
       id  
       "/od6/public/values?alt=json"))

;; this should be using a callback
(defn grab-spreadsheet-json-string [url]  
  (-> url
      http/get 
      deref
      :body))

(defn grab-json [json]
  (json/read-str json :key-fn keyword))

(defn googify-column [column]
  [(-> (str "gsx$" column) 
        keyword)
   (keyword column)])

(defn get-row-seq [& keys] 
  (-> "1HoGBhv9cgwcXKLFPExbezgJR8BGcaw9Gs5UrlhEiFRQ"
      make-spreadsheet-url
      grab-spreadsheet-json-string
      grab-json
      (get-in keys)))

(defn grab-rows [sel-rows rows]
  (reduce-kv (fn [m k v]
               (if-let [k (k sel-rows)]
                 (assoc m k (:$t v))
                   m)) {} rows))

(get-row-seq :feed :entry)

(let [sel-rows (into {} (map googify-column ["price" "strandname" "type"]))]
 (map (partial grab-rows sel-rows) (get-row-seq :feed :entry)))

