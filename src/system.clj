(ns system
  (:require [modular.maker :refer (make)]
            [sheet-component :refer (new-sheet-atom)]
            [com.stuartsierra.component :as component :refer (system-map system-using)]))

(defn new-base-system-map [& args]
  (system-map
   :sheet-atom (make new-sheet-atom {}
                     :sheet "1HoGBhv9cgwcXKLFPExbezgJR8BGcaw9Gs5UrlhEiFRQ"
                     :columns ["type" "price"]
                     :init {})))

(defn new-production-system []
  (component/system-using (new-base-system-map) {}))




