(ns ch02-data-types.core)

(def silly-string
  "I am immutable. I am a silly string.")

(defn encode-letter [s x]
  (let [code (Math/pow (+ x (int (first (char-array s)))) 2)]
    (str "#" (int code))))

(defn encode [s]
  (let [number-of-words (count (clojure.string/split s #" "))]
    (clojure.string/replace s #"\w"
                            (fn [s] (encode-letter s number-of-words)))))

(defn decode-letter [x y]
  (let [number (Integer/parseInt (subs x 1))
        letter (char (- (Math/sqrt number) y))]
    (str letter)))

(defn decode [s]
  (let [number-of-words (count (clojure.string/split s #" "))]
    (clojure.string/replace s #"\#\d+"
                            (fn [s] (decode-letter s number-of-words)))))

(encode "riz maulana")
;; => "#13456#11449#15376 #12321#9801#14161#12100#9801#12544#9801"

(decode "#13456#11449#15376 #12321#9801#14161#12100#9801#12544#9801")
;; => "riz maulana"

;; maps
(def favorite-fruit {:name "Kiwi" :color "Green" :kcal_per_100g 61
                     :distinguish_mark "Hairy"})

(get favorite-fruit :color)
;; => "Green"
(:name favorite-fruit)
;; => "Kiwi"
(:taste favorite-fruit "very tasty")
;; => "very tasty"
(favorite-fruit :distinguish_mark)
;; => "Hairy"

;; sets
(def supported-currencies #{"Dollar" "Japanese Yen" "Euro" "Indian Rupee" "British Pound"})
(get supported-currencies "Euro")
;; => "Euro"

(get supported-currencies "Swiss Franc")
;; => nil

(supported-currencies "Dollar")
;; => "Dollar"

;; ("British Pound" supported-currencies)
;; => error

(contains? supported-currencies "Japanese Yen")
;; => true

(conj supported-currencies "Monopoly Money" "Gulden")
;; => #{"British Pound" "Euro" "Dollar" "Monopoly Money" "Japanese Yen" "Gulden" "Indian Rupee"}

(disj supported-currencies "Euro" "Dollar")
;; => #{"British Pound" "Japanese Yen" "Indian Rupee"}

;; vectors
(get [:a :b :c] 0)
;; => :a

(get [:a :b :c] 10)
;; => nil

(def fibonacci [0 1 1 2 3 5 8])

(get fibonacci 5)
;; => 5
(fibonacci 6)
;; => 8
(conj fibonacci 13 21)
;; => [0 1 1 2 3 5 8 13 21]

(let [size (count fibonacci)
      last-number (last fibonacci)
      penultimate-number (fibonacci (- size 2))]
  (conj fibonacci (+ last-number penultimate-number)))
;; => [0 1 1 2 3 5 8 13]

;; lists
(def foo (list :a :b :c :d))

(first foo)
;; => :a
(rest foo)
;; => (:b :c :d)

(def my-todo (list "Feed the cat" "Clean the bathroom" "Save the world"))

(cons "Go to work" my-todo)
;; => ("Go to work" "Feed the cat" "Clean the bathroom" "Save the world")
(conj my-todo "Go to work")
;; => ("Go to work" "Feed the cat" "Clean the bathroom" "Save the world")
(conj my-todo "Go to work" "wash my socks")
;; => ("wash my socks" "Go to work" "Feed the cat" "Clean the bathroom" "Save the world")
(first my-todo)
;; => "Feed the cat"
(rest my-todo);; => ("Clean the bathroom" "Save the world")

(def language {:name "Clojure" :creator "Rich Hickey" :platforms ["Java" "JavaScript" ".NET"]})

(count language)
;; => 3

(count #{});; => 0

(seq language)
;; => ([:name "Clojure"] [:creator "Rich Hickey"] [:platforms ["Java" "JavaScript" ".NET"]])

(into [1 2 3 4] #{5 6 7 8})
;; => [1 2 3 4 7 6 5 8]

(into #{1 2 3 4} [5 6 7 8]);; => #{7 1 4 6 3 2 5 8}

(into #{} [1 2 3 4 4 3 3]);; => #{1 4 3 2}

(into {} [[:a 1] [:b 2] [:c 3]])
;; => {:a 1, :b 2, :c 3}

(into '() [1 2 3 4]);; => (4 3 2 1)

(concat '(1 2) '( 3 4))
;; => (1 2 3 4)
(into '(1 2) '(3 4))
;; => (4 3 1 2)

(concat #{1 2 3} #{1 2 3 4})
;; => (1 3 2 1 4 3 2)

(concat {:a 1} ["Hello"]);; => ([:a 1] "Hello")

(def alphabet #{:a :b :c :d :e :f})
;; => #'ch02-data-types.core/alphabet

alphabet;; => #{:e :c :b :d :f :a}

(sort alphabet)
;; => (:a :b :c :d :e :f)

(sort [3 7 5 1 9])
;; => (1 3 5 7 9)

(into [] (sort [3 5 7 1 2 4 9]));; => [1 2 3 4 5 7 9]

(conj language [:created 2007]);; => {:name "Clojure", :creator "Rich Hickey", :platforms ["Java" "JavaScript" ".NET"], :created 2007}

(assoc [:a :b :c :d] 2 :z);; => [:a :b :z :d]

(def gemstone-db
  {:ruby
   {:name "Ruby"
    :stock 480
    :sales [1990 3644 6376 4918 7882 6747 7495 8573 5097 1712]
    :properties {:dispersion 0.018
                 :hardness 9.0
                 :refractive-index [1.77 1.78]
                 :color "Red"}}})

;; get hardness
(get (get (get gemstone-db :ruby) :properties) :hardness);; => 9.0
(:hardness (:properties (:ruby gemstone-db)));; => 9.0
(get-in gemstone-db [:ruby :properties :hardness])
;; => 9.0

;; get durability/hardness fn
(defn durability
  [db gemstone]
  (get-in db [gemstone :properties :hardness]))

(durability gemstone-db :ruby);; => 9.0

;; change color
(assoc (:ruby gemstone-db) :properties {:color "Near colorless through pink etc..."});; => {:name "Ruby", :stock 480, :sales [1990 3644 6376 4918 7882 6747 7495 8573 5097 1712], :properties {:color "Near colorless through pink etc..."}}
(update (:ruby gemstone-db) :properties into {:color "Near colorlerss through pink etc..."});; => {:name "Ruby", :stock 480, :sales [1990 3644 6376 4918 7882 6747 7495 8573 5097 1712], :properties {:dispersion 0.018, :hardness 9.0, :refractive-index [1.77 1.78], :color "Near colorlerss through pink etc..."}}
(assoc-in gemstone-db [:ruby :properties :color] "Near colorless through pink etc...");; => {:ruby {:name "Ruby", :stock 480, :sales [1990 3644 6376 4918 7882 6747 7495 8573 5097 1712], :properties {:dispersion 0.018, :hardness 9.0, :refractive-index [1.77 1.78], :color "Near colorless through pink etc..."}}}

;; change color fn
(defn change-color
  [db gemstone new-color]
  (assoc-in gemstone-db [gemstone :properties :color] new-color))

(change-color gemstone-db :ruby "Some kind of red");; => {:ruby {:name "Ruby", :stock 480, :sales [1990 3644 6376 4918 7882 6747 7495 8573 5097 1712], :properties {:dispersion 0.018, :hardness 9.0, :refractive-index [1.77 1.78], :color "Some kind of red"}}}

;; sell a ruby: decrement the stock and add the client id to sales
(update-in gemstone-db [:ruby :stock] dec)
;; => {:ruby {:name "Ruby", :stock 479, :sales [1990 3644 6376 4918 7882 6747 7495 8573 5097 1712], :properties {:dispersion 0.018, :hardness 9.0, :refractive-index [1.77 1.78], :color "Red"}}}
(update-in gemstone-db [:ruby :sales] conj 999);; => {:ruby {:name "Ruby", :stock 480, :sales [1990 3644 6376 4918 7882 6747 7495 8573 5097 1712 999], :properties {:dispersion 0.018, :hardness 9.0, :refractive-index [1.77 1.78], :color "Red"}}}

;; sell function
(defn sell
  [db gemstone client-id]
  (let [clients-updated-db (update-in db [gemstone :sales] conj client-id)]
    (update-in clients-updated-db [gemstone :stock] dec)))

(sell gemstone-db :ruby 123);; => {:ruby {:name "Ruby", :stock 479, :sales [1990 3644 6376 4918 7882 6747 7495 8573 5097 1712 123], :properties {:dispersion 0.018, :hardness 9.0, :refractive-index [1.77 1.78], :color "Red"}}}

;;;;;;;; in-memory db
(def example-db
  {:clients {:data [{:id 1 :name "Bob" :age 30}
                    {:id 2 :name "Alice" :age 24}]
            :indexes {:id {1 0, 2 1}}},
   :fruits {:data [{:name "Lemon" :stock 10}
                   {:name "Coconut" :stock 3}]
           :indexes {:name {"Lemon" 0, "Coconut" 1}}},
   :purchases {:data [{:id 1 :user-id 1 :item "Coconut"}
                      {:id 1 :user-id 2 :item "Lemon"}]
              :indexes {:id {1 0, 2 1}}}})

(def memory-db (atom {}))
(defn read-db []
  @memory-db)
(defn write-db [new-db]
  (reset! memory-db new-db))

(defn create-table [table-name]
  (write-db (assoc (read-db)
                   table-name {:data [] :indexes {}})))
(create-table :clients)
(create-table :fruits)
;; => #'ch02-data-types.core/create-table

(defn drop-table [table-name]
  (let [db (dissoc (read-db) table-name)]
    (write-db db)))

(defn insert [table record id-key]
  (if-let [existing-record (select-*-where table id-key (id-key record))]
    (println (str "Record with " id-key ": " (id-key record) " already exists. Aborting."))
    (let [db (read-db)
          add-record-db (update-in db [table :data] conj record)
          new-index (dec (count (get-in add-record-db [table :data])))
          updated-db (update-in add-record-db [table :indexes id-key] assoc (id-key record) new-index)]
      (write-db updated-db))))

(defn select-* [table]
  (let [db (read-db)]
    (get-in db [table :data])))

(defn select-*-where
  [table-name field field-value]
  (let [db (read-db)
        index (get-in db [table-name :indexes field field-value])
        data (get-in db [table-name :data])]
    (get data index)))
