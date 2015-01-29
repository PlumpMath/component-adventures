#!/usr/bin/env boot

(set-env!
 :resource-paths #{"src"}
 :dependencies '[[http-kit "2.1.16"]
                 [org.clojure/clojure "1.7.0-alpha4"     :scope "provided"]
                 [org.clojure/data.json "0.2.5"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [boot/core           "2.0.0-rc2" :scope "provided"]])

(deftask build
  "Builds an uberjar of this project that can be run with java -jar"
  []
  (comp
   (aot :namespace '#{no-component})
   (pom :project 'component-adventure
        :version "1.0.0")
   (uber)
   (jar :main 'my_namespace)))

(defn -main [& args]
  (require 'no-component)
  (apply (resolve 'no-component/-main) args))
