(ns ch11-macros.core
  (:gen-class))

(defmacro minimal-macro []
  '(println "I'm trapped inside a macro"))
;; => #'ch11-macros.core/minimal-macro

(defn minimal-function []
  (println "I'm trapped inside a function"))
;; => #'ch11-macros.core/minimal-function

(defmacro mistaken-macro []
  (println "I'm trapped ... somewhere"))

(macroexpand '(minimal-macro))
;; => (println "I'm trapped inside a macro")
(macroexpand '(minimal-function))
;; => (minimal-function)
(macroexpand '(mistaken-macro))
;; => nil

(defmacro multi-minimal [n-times]
  (cons 'do (repeat n-times '(println "Macro"))))

(multi-minimal 3)
;; => nil
;; Macro
;; Macro
;; Macro

(macroexpand '(multi-minimal 3))
;; => (do (println "Macro") (println "Macro") (println "Macro"))

(defmacro parameterized-multi-minimal [n-times s]
  (concat (list 'let ['string-to-print s])
          (repeat n-times '(println string-to-print))))

(macroexpand '(parameterized-multi-minimal 3 "my text"))
;; => (let* [string-to-print "my text"] (println string-to-print) (println string-to-print) (println string-to-print))

(defmacro parameterized-with-syntax [n-times s]
  `(do ~@(repeat n-times `(println ~s))))

(macroexpand '(parameterized-with-syntax 3 "syntax quoting"))
;; => (do
;;      (clojure.core/println "syntax quoting")
;;      (clojure.core/println "syntax quoting")
;;      (clojure.core/println "syntax quoting"))


;; exercise 11.01
(defn validate-params
  [a b c d]
  (and
   (or (> a 5) (> a b))
   (or (= b a) (> b 5))
   (or (> a c) (> c 5) (= c b))
   (or (= a d) (> d 5))))

(defmacro and-ors [& or-exps]
  (let [groups (remove (partial = '(|)) (partition-by (partial = '|) or-exps))]
    `(and
      ~@(map (fn [g] `(or ~@g)) groups))))

(and-ors (> 5 3) (= 6 6) | (> 6 3) | (= 5 5 5))
;; => true

(and-ors
 (and-ors (= 3 3) | (= 5 5) (= 6 8))
 |
 (> 5 3) (= 6 6)
 |
 (> 6 3)
 |
 (= 5 5 5))
;; => true

(macroexpand-1 '(and-ors (> 5 3) (= 6 6) | (> 6 3) | (= 5 5 5)))
;; =>
;; (clojure.core/and
;;  (clojure.core/or (> 5 3) (= 6 6))
;;  (clojure.core/or (> 6 3))
;;  (clojure.core/or (= 5 5 5)))

;; exercise 11.02
(defn attributes [m]
  (clojure.string/join " "
                       (map (fn [[k v]]
                              (if (string? v)
                                (str (name k) "=\"" v "\"")
                                (name k)))
                            m)))

(defn ->closed-tag [tagname attrs]
  (if attrs
    (str "<" tagname " " (attributes attrs) "/>")
    (str "<" tagname "/>")))

(defn ->opening-tag [tagname attrs]
  (if attrs
    (str "<" tagname " " (attributes attrs) ">")
    (str "<" tagname ">")))

(defn ->end-tag [tagname]
  (str "</" tagname ">"))

(defn h1 [& content]
  (let [attrs?  (map? (first content))
        content-items (if attrs? (rest content)
                          content)]
    (if (empty? content-items)
      (->closed-tag "h1" (when attrs? (first content)))
      (str
        (->opening-tag "h1" (when attrs? (first content)))
        (apply str content-items)
        (->end-tag "h1")))))

(defn tag-fn [tagname]
  (fn [& content]
    (let [attrs?  (map? (first content))
          real-content-items (if attrs? (rest content)
                            content)
          content-items (mapcat (fn [i]
                                  (if (sequential? i)
                                    i
                                    [i])) real-content-items)]
      (if (empty? content-items)
        (->closed-tag tagname (when attrs? (first content)))

        (str
          (->opening-tag tagname (when attrs? (first content)))
          (apply str content-items)
          (->end-tag tagname))))))

(defmacro define-html-tags [& tags]
  `(do
     ~@(map (fn [tagname]
              `(def ~(symbol tagname) (tag-fn ~tagname)))
            tags)))

(define-html-tags "li" "h1" "h2" "h3" "h4" "h5" "p" "div" "span")

(div
 (h1 "First things first")
 (p {:class "intro"} "What is the best way to get started?"))
;; => "<div><h1>First things first</h1><p class=\"intro\">What is the best way to get started?</p></div>"


;; exercise 11.03
(defn subtag-fn [tagname subtag]
  (fn subtag-function-builder
    ([content]
     (subtag-function-builder nil content))
    ([attrs content]
     (str
      (->opening-tag tagname attrs)
      (apply str (map subtag content))
      (->end-tag tagname)))))

((subtag-fn "ul" li) {:class "my-class"} ["item 1" "item 2"])
;; => "<ul class=\"my-class\"><li>item 1</li><li>item 2</li></ul>"

((subtag-fn "ul" li) ["item 1" "item 2"])
;; => "<ul><li>item 1</li><li>item 2</li></ul>"

(defmacro define-html-list-tags [& tags-with-subtags]
  `(do
     ~@(map (fn [[tagname subtag]]
              `(do
                 (def ~(symbol tagname) (tag-fn ~tagname))
                 (def ~(symbol (str tagname "->" subtag))
                   (subtag-fn ~tagname ~(symbol subtag)))))
            tags-with-subtags)))

(macroexpand '(define-html-list-tags ["ul" "li"] ["ol" "li"]))
;; =>
(do
  (do
    (def ul (ch11-macros.core/tag-fn "ul"))
    (def ul->li (ch11-macros.core/subtag-fn "ul" li)))
  (do
    (def ol (ch11-macros.core/tag-fn "ol"))
    (def ol->li (ch11-macros.core/subtag-fn "ol" li))))

(define-html-list-tags ["ul" "li"] ["ol" "li"])

(ol->li ["item 1" "item 2"])
;; => "<ol><li>item 1</li><li>item 2</li></ol>"
(ol->li {:class "my-class"} ["item 1" "item 2"])
;; => "<ol class=\"my-class\"><li>item 1</li><li>item 2</li></ol>"

(defmacro define-html-list-tags-with-mapcat [& tags-with-subtags]
  `(do
     ~@(mapcat (fn [[tagname subtag]]
                 [`(def ~(symbol tagname) (tag-fn ~tagname))
                  `(def ~(symbol (str tagname "->" subtag))
                     (subtag-fn ~tagname ~(symbol subtag)))])
               tags-with-subtags)))

(macroexpand '(define-html-list-tags-with-mapcat ["ul" "li"] ["ol" "li"]))
;; =>
;; (do
;;   (def ul (ch11-macros.core/tag-fn "ul"))
;;   (def ul->li (ch11-macros.core/subtag-fn "ul" li))
;;   (def ol (ch11-macros.core/tag-fn "ol"))
;;   (def ol->li (ch11-macros.core/subtag-fn "ol" li)))


;;; variable capture
(defmacro parameterized-multi-minimal-wrong [n-times s]
  (cons 'do (repeat n-times '(println s))))

(let [s "wrong"]
  (parameterized-multi-minimal-wrong 5 "right"))
;; => nil
;; wrong
;; wrong
;; wrong
;; wrong
;; wrong

(defmacro let-number [[binding n] body]
  `(let [~(symbol (str binding "-as-string")) (str ~n)
         ~(symbol (str binding "-as-int")) (int ~n)
         ~(symbol (str binding "-as-double")) (double ~n)]
     ~body))

(let-number [my-int 5]
  (+ my-int-as-int 8))
;; => 13

(let-number [my-int 5]
  (str "the value is: " my-int-as-string))
;; => "the value is: 5"

(let [my-int-as-int 1000]
  (let-number [my-int (/ my-int-as-int 2)]
    (str "the result is: " my-int-as-double)))
;; => "the result is: 250.0"

(macroexpand-1 '(let-number [my-int (/ my-int-as-int 2)]
                  (str "the result is: " my-int-as-double)))
;; =>
;; (clojure.core/let [my-int-as-string (clojure.core/str (/ my-int-as-int 2))
;;                    my-int-as-int (clojure.core/int (/ my-int-as-int 2))
;;                    my-int-as-double (clojure.core/double (/ my-int-as-int 2))]
;;   (str "the result is: " my-int-as-double))
;; my-int-as-int is divided by 2 twice, so the result is 250.0

;; avoiding variable capture with gensym
(defmacro let-number-fix [[binding n] body]
  `(let [result# ~n
         ~(symbol (str binding "-as-string")) (str result#)
         ~(symbol (str binding "-as-int")) (int result#)
         ~(symbol (str binding "-as-double")) (double result#)]
     ~body))

(let [my-int-as-int 1000.0]
  (let-number-fix [my-int (/ my-int-as-int 2)]
    (str "the result is: " my-int-as-double)))
;; => "the result is: 500.0"

;; gensym: generated symbol
`(result#)
;; => (result__8148__auto__)

`(result#)
;; => (result__8152__auto__)

`(= result# result#)
;; => (clojure.core/= result__8157__auto__ result__8157__auto__)

(defmacro bad-let-number [[binding n] body]
  `(let [~'result ~n
         ~(symbol (str binding "-as-string")) (str ~'result)
         ~(symbol (str binding "-as-int")) (int ~'result)
         ~(symbol (str binding "-as-double")) (double ~'result)]
     ~body))
;; => #'ch11-macros.core/bad-let-number

(let [result 42]
  (bad-let-number [my-int 1000]
                  (= result 1000)))
;; => true

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
