(ns ch11-macros.core
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [semantic-csv.core :as sc])
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

;; exercise 11.04
(defn wrap-fn-body [fn-name tx-fn b]
  (let [arg-list (first b)
        fn-body (rest b)]
    (when-not (first (filter #(= % 'client-id) arg-list))
      (throw (ex-info "missing client-id argument" {})))
    `(~arg-list
      (let [start-time# (System/nanoTime)]
        (try
          (let [result# (do ~@fn-body)]
            (~tx-fn {:name ~(name fn-name)
                     :client-id ~'client-id
                     :status :complete
                     :start-time start-time#
                     :end-time (System/nanoTime)})
            result#)
          (catch Exception e#
            (~tx-fn {:name ~(name fn-name)
                     :client-id ~'client-id
                     :status :error
                     :start-time start-time#
                     :end-time (System/nanoTime)})
            (throw e#)))))))

(defmacro defmonitored
  [fn-name tx-fn & args-and-body]
  (let [pre-arg-list (take-while (complement sequential?) args-and-body)
        fn-content (drop-while (complement sequential?) args-and-body)
        fn-bodies (if (vector? (first fn-content))
                    `(~fn-content)
                    fn-content)]
    `(defn ~fn-name ~@pre-arg-list
       ~@(map (partial wrap-fn-body fn-name tx-fn) fn-bodies))))

(defmonitored my-func println [client-id m] (assoc m :client client-id))

(my-func 32 {:data 123})
;; => {:data 123, :client 32}
;; {:client-id 32, :name my-func, :start-time 4138545660618, :end-time 4138545665547, :status :complete}

(defmonitored exception-func println [client-id] (throw (ex-info "Boom!" {})))

;;(exception-func 5)
;; => 
;; {:client-id 5, :name exception-func, :start-time 4208513192769, :end-time 4208513746098, :status :error}
;; Execution error (ExceptionInfo) at ch11-macros.core/exception-func (form-init14215583336918779527.clj:326).
;; Boom!

;;(defmonitored no-client-func println [no-client-id] (+ 1 1))
;; => 
;; Syntax error macroexpanding clojure.core/defn at (src/ch11_macros/core.clj:334:1).
;; missing client-id argument

;;;;;;;
(defmacro fn-context [v & symbol-fn-pairs]
  `(let [v# ~v]
     ~@(map (fn [[sym f]]
              `(defn ~sym [x#]
                 (~f v# x#))) (partition 2 symbol-fn-pairs))))

;;(fn-context 5 adder + subtractor - multiplier *)
;; => 
;; Syntax error compiling at (*cider-repl clj-workshop/ch11-macros:localhost:46665(clj)*:222:19).
;; Unable to resolve symbol: v__10509__auto__ in this context

(macroexpand-1 '(fn-context 5 adder + subtractor - multiplier *))
;; (clojure.core/let
;;     [v__10510__auto__ 5]
;;   (clojure.core/defn
;;     adder
;;     [x__10508__auto__]
;;     (+ v__10509__auto__ x__10508__auto__))
;;   (clojure.core/defn
;;     subtractor
;;     [x__10508__auto__]
;;     (- v__10509__auto__ x__10508__auto__))
;;   (clojure.core/defn
;;     multiplier
;;     [x__10508__auto__]
;;     (* v__10509__auto__ x__10508__auto__)))

(defmacro fn-context-fix [v & symbol-fn-pairs]
  (let [common-val-gensym (gensym "common-val-")]
    `(let [~common-val-gensym ~v]
       ~@(map (fn [[sym f]]
                `(defn ~sym [x#]
                   (~f ~common-val-gensym x#))) (partition 2 symbol-fn-pairs)))))

(fn-context-fix 5 adder + subtractor - multiplier *)

(adder 5)
;; => 10
(subtractor 42)
;; => -37
(multiplier 10)
;; => 50


;; activity 11.01
(defn maybe-select-keys [m maybe-keys]
  (if (seq maybe-keys)
    (select-keys m maybe-keys)
    m))

(defmacro with-tennis-csv [csv casts fields & forms]
  `(with-open [reader# (io/reader ~csv)]
     (->> (csv/read-csv reader#)
          sc/mappify
          (sc/cast-with ~casts)
          ~@forms
          (map #(maybe-select-keys % ~fields))
          doall)))

(defn blowouts [csv threshold]
  (with-tennis-csv csv
    {:winner_games_won sc/->int :loser_games_won sc/->int}
    [:winner_name :loser_name :games_diff]
    (map #(assoc % :games_diff (- (:winner_games_won %) (:loser_games_won %))))
    (filter #(> (:games_diff %) threshold))))

(blowouts "resources/match_scores_1991-2016_unindexed_csv.csv" 16)
;; ({:winner_name "Jean-Philippe Fleurian",
;;   :loser_name "Renzo Furlan",
;;   :games_diff 17}
;;  {:winner_name "Todd Witsken", :loser_name "Kelly Jones", :games_diff 17}
;;  {:winner_name "Nicklas Kulti", :loser_name "German Lopez", :games_diff 17}
;;  {:winner_name "Andrei Medvedev", :loser_name "Lars Koslowski", :games_diff 17}
;;  {:winner_name "Sergi Bruguera",
;;   :loser_name "Thierry Champion",
;;   :games_diff 18}
;;  {:winner_name "Michael Chang", :loser_name "Gianluca Pozzi", :games_diff 17}
;;  {:winner_name "Lleyton Hewitt", :loser_name "Alex Corretja", :games_diff 17}
;;  {:winner_name "Andre Agassi", :loser_name "Hyung-Taik Lee", :games_diff 17}
;;  {:winner_name "Andy Murray", :loser_name "Alberto Martin", :games_diff 17}
;;  {:winner_name "David Ferrer", :loser_name "Fabrice Santoro", :games_diff 17}
;;  {:winner_name "Tomas Berdych", :loser_name "Robert Smeets", :games_diff 17})

(with-tennis-csv "resources/match_scores_1991-2016_unindexed_csv.csv" {} [:winner_name] (filter #(= "Roger Federer" (:loser_name %))))
;; ({:winner_name "Lucas Arnold Ker"}
;;  {:winner_name "Jan Siemerink"}
;;  {:winner_name "Andre Agassi"}
;;  {:winner_name "Arnaud Clement"}
;;  {:winner_name "Yevgeny Kafelnikov"}
;;  {:winner_name "Kenneth Carlsen"}
;;  {:winner_name "Vincent Spadea"}
;;  ...
;;  )
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
