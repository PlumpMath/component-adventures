(ns core
  (:require [system :refer (new-production-system)]
            [com.stuartsierra.component :refer (start)])
  (:gen-class))

 ;:gen-class here means this namespace will be compiled into a .class
                                        ;file if the aot task is run



(defn -main [& args]                  
  (let [system (start
                (new-production-system))]

    (:sheet-atom system)))

