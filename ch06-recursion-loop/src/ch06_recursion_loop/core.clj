(ns ch06-recursion-loop.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

;; doseq only perform side effects and return nil
(doseq [n (range 5)]
  (println (str "Line " n)))
;; Line 0
;; Line 1
;; Line 2
;; Line 3
;; Line 4
;; => nil

;; print only odd numbers
(doseq [n (range 5)]
  (when (odd? n)
    (println (str "Line " n))))
;; Line 1
;; Line 3
;; => nil

;; or
;; shapet the data first, and then use them.
(doseq [n (filter odd? (range 5))]
  (println (str "Line " n)))
;; Line 1
;; Line 3
;; => nil

;; looping shortcuts
(take 5 (repeat "he"))
;; => ("he" "he" "he" "he" "he")

;; example: initialize default value with repeat
(zipmap [:score :hits :friends :level :energy :boost] (repeat 0))
;; => {:score 0, :hits 0, :friends 0, :level 0, :energy 0, :boost 0}

;; generate list of random integers
(take 10 (repeatedly (partial rand-int 100)))
;; => (13 91 19 51 42 99 54 70 24 19)

;; or
(repeatedly 10 (partial rand-int 100))
;; => (51 81 3 7 39 57 43 21 73 50)

;; seq of balance each month with annual rate 1%
(defn savings [principal yearly-rate]
  (let [monthly-rate (+ 1 (/ yearly-rate 12))]
    (iterate (fn [p] (* p monthly-rate)) principal)))

(take 13 (savings 1000 0.01))
;; => (1000 1000.8333333333333 1001.667361111111 1002.5020839120368 1003.3375023152968 1004.1736169005594 1005.0104282479765 1005.847936938183 1006.6861435522981 1007.5250486719249 1008.3646528791514 1009.2049567565506 1010.045960887181)

;; exercise 6.01
(def grocery-articles [{:name "Flour"
                        :weight 1000
                        :max-dimension 140}
                       {:name "Bread"
                        :weight 350
                        :max-dimension 250}
                       {:name "Potatoes"
                        :weight 2500
                        :max-dimension 340}
                       {:name "Pepper"
                        :weight 85
                        :max-dimension 90}
                       {:name "Ice cream"
                        :weight 450
                        :max-dimension 200}
                       {:name "Green beans"
                        :weight 300
                        :max-dimension 120}
                       {:name "Olive oil"
                        :weight 400
                        :max-dimension 280}])

(defn article-stream [n]
  (repeatedly n #(rand-nth grocery-articles)))

(article-stream 10)
;; => ({:name "Potatoes", :weight 2500, :max-dimension 340} {:name "Olive oil", :weight 400, :max-dimension 280} {:name "Bread", :weight 350, :max-dimension 250} {:name "Olive oil", :weight 400, :max-dimension 280} {:name "Olive oil", :weight 400, :max-dimension 280} {:name "Pepper", :weight 85, :max-dimension 90} {:name "Potatoes", :weight 2500, :max-dimension 340} {:name "Pepper", :weight 85, :max-dimension 90} {:name "Bread", :weight 350, :max-dimension 250} {:name "Potatoes", :weight 2500, :max-dimension 340})

(article-stream 5)
;; => ({:name "Bread", :weight 350, :max-dimension 250} {:name "Olive oil", :weight 400, :max-dimension 280} {:name "Ice cream", :weight 450, :max-dimension 200} {:name "Olive oil", :weight 400, :max-dimension 280} {:name "Green beans", :weight 300, :max-dimension 120})

;; recursion
(defn recursive-sum [so-far numbers]
  (if (first numbers)
    (recursive-sum (+ so-far (first numbers)) (next numbers))
    so-far))

(recursive-sum 0 [300 25 8])
;; => 333

;; exercise 6.02
(defn full-bag? [items]
  (let [weight (apply + (map :weight items))
        size (apply + (map :max-dimension items))]
    (or (> weight 3200)
        (> size 800))))

(full-bag? (article-stream 10))
;; => true
(full-bag? (article-stream 1))
;; => false
(full-bag? (article-stream 1000))
;; => true
(full-bag? '())
;; => false

(defn bag-sequences* [{:keys [current-bag bags] :as acc} stream]
  (cond
    (not stream) (conj bags current-bag)
    (full-bag? (conj current-bag (first stream)))
    (bag-sequences* (assoc acc :current-bag [(first stream)]
                           :bags (conj bags current-bag))
                    (next stream))
    :otherwise (bag-sequences* (update acc :current-bag conj (first stream))
                               (next stream))))

(defn bag-sequences [stream]
  (bag-sequences* {:bags []
                  :current-bag []} stream))

(bag-sequences (article-stream 12))
;; [[{:name "Pepper", :weight 85, :max-dimension 90}
;;   {:name "Pepper", :weight 85, :max-dimension 90}
;;   {:name "Flour", :weight 1000, :max-dimension 140}
;;   {:name "Green beans", :weight 300, :max-dimension 120}
;;   {:name "Flour", :weight 1000, :max-dimension 140}]
;;  [{:name "Olive oil", :weight 400, :max-dimension 280}
;;   {:name "Flour", :weight 1000, :max-dimension 140}]
;;  [{:name "Potatoes", :weight 2500, :max-dimension 340}
;;   {:name "Green beans", :weight 300, :max-dimension 120}
;;   {:name "Green beans", :weight 300, :max-dimension 120}]
;;  [{:name "Potatoes", :weight 2500, :max-dimension 340}]
;;  [{:name "Potatoes", :weight 2500, :max-dimension 340}]]

;; recur
(defn safe-recursive-sum [so-far numbers]
  (if (first numbers)
    (recur (+ so-far (first numbers)) (next numbers))
    so-far))

(safe-recursive-sum 0 (range 1000))
;; => 499500

;; exercise 6.03
(defn robust-bag-sequences* [{:keys [current-bag bags] :as acc} stream]
  (cond
    (not stream) (conj bags current-bag)
    (full-bag? (conj current-bag (first stream)))
    (recur (assoc acc
                  :current-bag [(first stream)]
                  :bags (conj bags current-bag))
           (next stream))
    :otherwise
    (recur (assoc acc :current-bag (conj current-bag (first stream)))
           (next stream))))

(defn robust-bag-sequences [stream]
  (robust-bag-sequences* {:bags []
                          :current-bag []}
                         stream))

(def bags (robust-bag-sequences (article-stream 1000000)))

(count bags)
;; => 342397
(first bags)
;; => [{:name "Green beans", :weight 300, :max-dimension 120} {:name "Bread", :weight 350, :max-dimension 250} {:name "Potatoes", :weight 2500, :max-dimension 340}]

;; loop
(def process identity)
(defn grocery-verification [input-items]
  (loop [remaining-items input-items
         processed-items []]
    (if (not (seq remaining-items))
      processed-items
      (recur (next remaining-items)
             (conj processed-items (process (first remaining-items)))))))

;; exercise 6.04
(defn looping-robust-bag-sequences [stream]
  (loop [remaining-stream stream
         acc {:current-bag []
              :bags []}]
    (let [{:keys [current-bag bags]} acc]
      (cond
        (not remaining-stream) (conj bags current-bag)
        (full-bag? (conj current-bag (first remaining-stream)))
        (recur (next remaining-stream)
               (assoc acc :current-bag [(first remaining-stream)]
                      :bags (conj bags current-bag)))
        :otherwise
        (recur (next remaining-stream)
               (assoc acc :current-bag (conj current-bag (first remaining-stream))))))))

(looping-robust-bag-sequences (article-stream 8))
;; => [[{:name "Pepper", :weight 85, :max-dimension 90} {:name "Flour", :weight 1000, :max-dimension 140} {:name "Olive oil", :weight 400, :max-dimension 280} {:name "Ice cream", :weight 450, :max-dimension 200}] [{:name "Olive oil", :weight 400, :max-dimension 280} {:name "Green beans", :weight 300, :max-dimension 120} {:name "Bread", :weight 350, :max-dimension 250} {:name "Pepper", :weight 85, :max-dimension 90}]]

;; tail recursion
(def nested [5 12 [3 48 16] [1 [53 8 [[4 43]] [8 19 3]] 29]])

;; (defn naive-tree-sum [so-far x]
;;   (cond
;;     (not x) so-far
;;     (integer? (first x)) (recur (+ so-far (first x)) (next x))
;;     (or (seq? (first x)) (vector? (first x)))
;;     (recur (recur so-far (first x)) (next x)))) ;; error on this

(defn less-naive-tree-sum [so-far x]
  (cond
    (not x) so-far
    (integer? (first x)) (less-naive-tree-sum (+ so-far (first x)) (next x))
    (or (seq? (first x)) (vector? (first x)))
    (less-naive-tree-sum (less-naive-tree-sum so-far (first x)) (next x))))

(less-naive-tree-sum 0 nested)
;; => 252
