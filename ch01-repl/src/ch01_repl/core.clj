(ns ch01-repl.core)

(defn co2-estimate
  "Estimate Co2 ppm in the atmosphere"
  [year]
  (let [base-year 2006
        base-co2 382
        year-diff (- year base-year)]
    (+ base-co2 (* 2 year-diff))))

(let [x 50]
  (if (or (<= 1 x 100) (= 0 (mod x 100)))
    (println "Valid")
    (println "Invalid")))

(defn meditate
  "check your state of mind"
  [calmness-level s]
  (println "Clojure Meditate v2.0")
  (cond
    (< calmness-level 5) (str (clojure.string/upper-case s) ", I TELL YA!")
    (<= 5 calmness-level 9) (clojure.string/capitalize s)
    (= calmness-level 10) (clojure.string/reverse s)))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
