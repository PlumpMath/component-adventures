(ns sheet-component
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [sheet :refer [tap-sheet]]
            [com.stuartsierra.component :as component]
            [schema.core :as s]
            [clojure.core.async :as async :refer [go chan pub put! <!]]))

;; seems like the ability to write to a spreadsheet requires downloading jars 
;; so i am punting on this issue

;; i literally copy and pasted this from dylan buttman for my own learning
;; https://github.com/pleasetrythisathome

(defprotocol IPersist
  (save! [this])
  (init! [this]))

(defn sheet? [atom]
  (:sheet (meta atom)))

(extend-type clojure.lang.Atom
  component/Lifecycle
  (start [this]
    (when (sheet? this)
      (let [sheet-id (:sheet (meta this))
            columns (:columns (meta this))
            value (tap-sheet sheet-id columns)]
        (when-not (nil? value)
          (reset! this value)))
      (add-watch this :persist (fn [_key _ref old-value new-value]
                                 (when-not (= old-value new-value)
                                   (save! this)))))
    this)
  (stop [this]
    (when (sheet? this)
      (remove-watch this :persist))
    this)
  IPersist
  (init! [this]
    (when (sheet? this)
      (reset! this (:init (meta this))))))

(def persistent-atom-schema
  {:sheet s/Str
   :columns [s/Str]
   :init s/Any})

(defn new-sheet-atom
  [& {:as opts}]
  (println opts)
  (let [{:keys [sheet columns init]} (->> opts
                                     (merge {})
                                     (s/validate persistent-atom-schema))]
    (atom init :meta {:sheet sheet
                      :columns columns
                      :init init})))
