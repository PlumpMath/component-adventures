(ns sheet
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

(defn grab-spreadsheet-json-string [url]  
  (-> url
      http/get 
      deref
      :body))

(defn json->edn [json]
  (json/read-str json :key-fn keyword))

(defn googify-column [column]
  [(-> "gsx$"
       (str  column) 
       keyword)
   (keyword column)])

(defn get-row-seq [spreadsheet-id  & keys] 
  (-> spreadsheet-id
      make-spreadsheet-url
      grab-spreadsheet-json-string
      json->edn
      (get-in keys)))

(defn grab-rows 
  ([rows]
   (reduce-kv (fn [m k v]
                (let [k (->> k
                            name
                            (drop 4)
                            (apply str)
                            keyword)]
                  (if-let [v (:$t v)]
                      (assoc m k v)
                      m))) {} rows))
  ([sel-rows rows]
                 (reduce-kv (fn [m k v]
                              (if-let [k (k sel-rows)]
                                (assoc m k (:$t v))
                                m)) {} rows)))

(defn tap-sheet [spreadsheet-id columns]
  (let [sel-rows (map googify-column columns)
        sel-rows-m (into {} sel-rows)
        grab-rows (if (empty? sel-rows)  
                    grab-rows
                    (partial grab-rows sel-rows-m) )]
    (map grab-rows (get-row-seq spreadsheet-id :feed :entry))))


(tap-sheet "1HoGBhv9cgwcXKLFPExbezgJR8BGcaw9Gs5UrlhEiFRQ" ["price"])
