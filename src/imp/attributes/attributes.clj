
(ns imp.attributes.clojure
  (:gen-class
    :name imp.attributes.clojure
    :methods [#^{:static true} 
               [rulesToMap [Polylist] IPersistantMap]
               [sumList [Term] IntegerTerm]])
  (:use
    polya
    jp.ac.kobe_u.cs.prolog.lang
    jp.ac.kobe_u.cs.prolog.builtin)
  )


(defn -rulesToMap [l m] (rules-to-map l {})) ;; wrapper

;; rules-to-map turns a list of rules into a map from the names
;; to the expansions and probabilities.
(defn rules-to-map [polylist map]
  (if (.isEmpty polylist)
    map ;; base case - return the accumulator
    (recur
      (.rest polylist)
      (let [rule (.first polylist)
            name (.first rule) ;; the symbol name
            contents (.rest rule)]
        (if (not (contains? map name))
          (assoc map name #{contents}) ;; key a set of possible expansions
          (assoc map name
            (conj
              (get map name)
              contents)))))))


