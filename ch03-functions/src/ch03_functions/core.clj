(ns ch03-functions.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

;; destructuring
;; coordinate
(defn print-coords [coords]
  (let [lat (first coords)
        lon (last coords)]
    (println (str "Latitude: " lat " - " "Longitude: " lon))))

(defn print-coords' [coords]
  (let [[lat lon] coords]
    (println (str "Latitude: " lat " - " "Longitude: " lon))))

(let [[a b c] [1 2 3]]
  (println a b c))

(defn print-coords-map [airport]
  (let [lat (:lat airport)
        lon (:lon airport)
        name (:name airport)]
    (println (str name " is located at Latitude: " lat " - " "Longitude: " lon))))

(defn print-coords-map' [airport]
  (let [{lat :lat lon :lon airport-name :name} airport]
    (println (str airport-name " is located at Latitude: " lat " - " "Longitude: " lon))))

(defn print-coords-map'' [airport]
  (let [{:keys [lat lon name]} airport]
    (println (str name " is located at Latitude: " lat " - " "Longitude: " lon))))

;; exercise 3.01
(def booking [1425, "Bob Smith", "Allergic to unsalted peanuts only",
              [[48.9615, 2.4372], [37.742, -25.6976]]
              [[37.742, -25.6976], [48.9615, 2.4372]]])

(let [[id cust-name sensitive-info flight1 flight2 flight3] booking]
  (println id cust-name flight1 flight2 flight3))
;; 1425 Bob Smith [[48.9615 2.4372] [37.742 -25.6976]] [[37.742 -25.6976] [48.9615 2.4372]] nil


(let [[_ cust-name _ flight1 flight2 flight3] booking]
  (println cust-name flight1 flight2 flight3))
;; Bob Smith [[48.9615 2.4372] [37.742 -25.6976]] [[37.742 -25.6976] [48.9615 2.4372]] nil

(let [[_ cust-name _ & flights] booking]
  (println (str cust-name " booked " (count flights) " flights.")))
;; Bob Smith booked 2 flights.

(defn print-flight [flight]
  (let [[[lat1 lon1] [lat2 lon2]] flight]
    (println (str "Flying from: Lat " lat1 " Lon " lon1 " Flying to: Lat " lat2 " Lon " lon2))))

(defn print-flight' [flight]
  (let [[departure arrival] flight
        [lat1 lon1] departure
        [lat2 lon2] arrival]
    (println (str "Flying from: Lat " lat1 " Lon " lon1 " Flying to: Lat " lat2 " Lon " lon2))))

(defn print-booking [booking]
  (let [[_ customer-name _ & flights] booking]
    (println (str customer-name " booked " (count flights) " flights."))
    (let [[flight1 flight2 flight3] flights]
      (when flight1 (print-flight' flight1))
      (when flight2 (print-flight' flight2))
      (when flight3 (print-flight' flight3)))))

(print-booking booking)
;; Bob Smith booked 2 flights.
;; Flying from: Lat 48.9615 Lon 2.4372 Flying to: Lat 37.742 Lon -25.6976
;; Flying from: Lat 37.742 Lon -25.6976 Flying to: Lat 48.9615 Lon 2.4372

;; exercise 3.02
(def mapjet-booking
  {:id 8773
  :customer-name "Alice Smith"
  :catering-notes "Vegetarian on Sundays"
  :flights [
            {
             :from {:lat 48.9615 :lon 2.4372 :name "Paris Le Bourget Airport"},
             :to {:lat 37.742 :lon -25.6976 :name "Ponta Delgada Airport"}},
            {
             :from {:lat 37.742 :lon -25.6976 :name "Ponta Delgada Airport"},
             :to {:lat 48.9615 :lon 2.4372 :name "Paris Le Bourget Airport"}}]})

(let [{:keys [customer-name flights]} mapjet-booking]
  (println (str customer-name " booked " (count flights) " flights.")))
;; Alice Smith booked 2 flights.

(defn print-mapjet-flight [flight]
  (let [{:keys [from to]} flight
        {lat1 :lat lon1 :lon} from
        {lat2 :lat lon2 :lon} to]
    (println (str "Flying from: Lat " lat1 " Lon " lon1 " Flying to: Lat " lat2 " Lon " lon2))))

(print-mapjet-flight (first (:flights mapjet-booking)))
;; Flying from: Lat 48.9615 Lon 2.4372 Flying to: Lat 37.742 Lon -25.6976

;; nesting associative destructuring
(defn print-mapjet-flight' [flight]
  (let [{{lat1 :lat lon1 :lon} :from,
         {lat2 :lat lon2 :lon} :to} flight]
    (println (str "Flying from: Lat " lat1 " Lon " lon1 " Flying to: Lat " lat2 " Lon " lon2))))

(defn print-mapjet-booking [booking]
  (let [{:keys [customer-name flights]} booking]
    (println (str customer-name " booked " (count flights) " flights."))
    (let [[flight1 flight2 flight3] flights]
      (when flight1 (print-mapjet-flight' flight1))
      (when flight2 (print-mapjet-flight' flight2))
      (when flight3 (print-mapjet-flight' flight3)))))

(print-mapjet-booking mapjet-booking)
;; Alice Smith booked 2 flights.
;; Flying from: Lat 48.9615 Lon 2.4372 Flying to: Lat 37.742 Lon -25.6976
;; Flying from: Lat 37.742 Lon -25.6976 Flying to: Lat 48.9615 Lon 2.4372

;;;;; advanced call signature
;; destructuring function parameters
(defn print-flight-desparam
  [[[lat1 lon1] [lat2 lon2]]]
    (println (str "Flying from: Lat " lat1 " Lon " lon1 " Flying to: Lat " lat2 " Lon " lon2)))

(defn print-mapjet-flight-desparam
  [{{lat1 :lat lon1 :lon} :from, {lat2 :lat lon2 :lon} :to}]
    (println (str "Flying from: Lat " lat1 " Lon " lon1 " Flying to: Lat " lat2 " Lon " lon2)))

;;; arity overloading
(defn no-overloading []
  (print "This is no-overloading function"))

(defn overloading
  ([] "No argument")
  ([a] (str "One argument: " a))
  ([a b] (str "Two arguments: a:" a " b: " b)))

(def weapon-damage {:fists 10 :staff 35 :sword 100 :cast-iron-saucepan 150})

(defn strike
  ([enemy] (strike enemy :fists))
  ([enemy weapon]
   (let [damage (weapon weapon-damage)]
     (update enemy :health - damage))))

(strike {:name "hunter" :health 100})
;; {:name "hunter", :health 90}
(strike {:name "hunter" :health 100} :sword)
;; {:name "hunter", :health 0}

;; variadic functions
;; function that take a variable number of args
(defn welcome
  [player & friends]
  (println (str "Welcome, " player "!"))
  (when (seq friends)
    (println (str "Sending " (count friends) " friend request to the following player: " (clojure.string/join ", " friends)))))

;; exercise 3.03
(defn strike'
  "with one argument, strike a target with a default :fists `weapon`.
  with two args, strike a target with `weapon`.
  strike will heal a target that belongs to the gnomes camp."
  ([target] (strike' target :fists))
  ([{:keys [camp armor], :or {armor 0}, :as target} weapon]
   (let [points (weapon weapon-damage)]
     (if (= :gnomes camp)
       (update target :health + points)
       (let [damage (* points (- 1 armor ))]
         (update target :health - damage))))))

(def enemy {:name "Zulkaz" :health 250 :camp :trolls :armor 0.8})

(strike' enemy :sword)
;; {:name "Zulkaz", :health 150, :camp :trolls}

(def ally {:name "Carla" :health 80 :camp :gnomes})

(strike' ally :sword)
;; {:name "Carla", :health 180, :camp :gnomes}

(strike' enemy :cast-iron-saucepan)
;; {:name "Zulkaz", :health 220.0, :camp :trolls, :armor 0.8}

(strike' enemy)
;; {:name "Zulkaz", :health 248.0, :camp :trolls, :armor 0.8}

(strike' enemy :cast-iron-saucepan)
;;{:name "Zulkaz", :health 220.0, :camp :trolls, :armor 0.8}

(strike' ally :staff)
;;{:name "Carla", :health 115, :camp :gnomes}

;;; higher order programming
;; first class function
(update {:item "Tomato" :price 1.0} :price (fn [x] (/ x 2)))
;; {:item "Tomato", :price 0.5}
(update {:item "Tomato" :price 1.0} :price / 2)
;; {:item "Tomato", :price 0.5}
(update {:item "Tomato" :fruit false} :fruit not)
;; {:item "Tomato", :fruit true}

(defn operate [f x]
  (f x))

(operate inc 2)
;; 3
(operate clojure.string/upper-case "hehe")
;; "HEHE"

(defn operate' [f & args]
  (apply f args))

(defn random-fn []
  (first (shuffle [+ - * /])))

((random-fn) 2 3)
;; => 2/3
;; => 6
;; => 5
;; => 6
;; => 5

;; partial function
(def marketing-adder (partial + 0.99))

(marketing-adder 10 5)
;; => 15.99

(def format-price (partial str "$"))

(format-price "100")
;; => "$100"

(format-price 10 28)
;; => "$1028"
;; => "$10"

;; composing function
(def sample (comp first shuffle))

(sample [1 2 3 4]);; => 1

(def checkout (comp (partial str "Only ") format-price marketing-adder))

(checkout 10 5 15 6 9)
;; => "Only $45.99"

(fn [s] (str "Hello" s))
;; same as
#(str "Hello" %)

(fn [x y] (* (+ x 10) (+ y 20)))
;; same as
#(* (+ %1 10) (+ %2 20))

(#(str %1 " " %2 " " %3) "1st" "2nd" "3rd");; => "1st 2nd 3rd"

;;; exercise 3.04
(def weapon-fn-map
  {:fists (fn [health]
            (if (< health 100)
              (- health 10)
              health))
   :staff (partial + 35)
   :sword #(- % 100)
   :cast-iron-saucepan #(- % 100 (rand-int 50))
   :sweet-potato identity})

((weapon-fn-map :fists) 150);; => 150
((weapon-fn-map :fists) 50)
;; => 40
((weapon-fn-map :staff) 150)
;; => 185
((weapon-fn-map :sword) 142);; => 42
((weapon-fn-map :cast-iron-saucepan) 200)
;; => 62
((weapon-fn-map :cast-iron-saucepan) 200)
;; => 77

(defn strike''
  "With one argument, strike with :fists `weapon`.
  With two args, strike a target with `weapon` and return target entity"
  ([target] (strike'' target :fists))
  ([target weapon]
   (let [weapon-fn (weapon weapon-fn-map)]
     (update target :health weapon-fn))))

(def enemy-arnold {:name "Arnold" :health 250})

(strike'' enemy-arnold :sweet-potato)
;; => {:name "Arnold", :health 250}
(strike'' enemy-arnold :sword)
;; => {:name "Arnold", :health 150}
(strike'' enemy-arnold :cast-iron-saucepan)
;; => {:name "Arnold", :health 109}
(strike'' (strike'' enemy-arnold :sword) :cast-iron-saucepan)
;; => {:name "Arnold", :health 22}

(update enemy-arnold :health (comp (:sword weapon-fn-map) (:cast-iron-saucepan weapon-fn-map)))
;; => {:name "Arnold", :health 11}

(defn mighty-strike
  "Strike a `target` with all weapons"
  [target]
  (let [weapon-fn (apply comp (vals weapon-fn-map))]
    (update target :health weapon-fn)))

(mighty-strike enemy-arnold)
;; => {:name "Arnold", :health 65}
(mighty-strike enemy-arnold);; => {:name "Arnold", :health 31}

;;; multimethod
;; (defmulti strike-mult
;;  (fn [m] (get m :weapon)))
;; or
(defmulti strike-mult :weapon) ;; :weapon is dispatch function

(defmethod strike-mult :sword ;; dispatch value
  [{{:keys [:health]} :target}] ;; fn argument
  (- health 100))

(defmethod strike-mult :cast-iron-saucepan
  [{{:keys [:health]} :target}]
  (- health 100 (rand-int 50)))


(strike-mult {:weapon :sword, :target {:health 200}})
;; => 100
(strike-mult {:weapon :cast-iron-saucepan, :target {:health 200}})
;; => 63

;;  (strike-mult {:weapon :spoon, :target {:health 200}})
;; =>
;; Execution error (IllegalArgumentException) at ch03-functions.core/eval8725 (form-init8542074451774964884.clj:335).
;; No method in multimethod 'strike-mult' for dispatch value: :spoon

;; default dispatch value
(defmethod strike-mult :default [{{:keys [:health]} :target}] health)

(strike-mult {:weapon :spoon, :target {:health 200}})
;; => 200

;;; exercise 3.05
(def player {:name "Lea" :health 200 :position {:x 10 :y 10 :facing :north}})

;; move function
(defmulti move (comp :facing :position))

(defmethod move :north
  [entity]
  (update-in entity [:position :y] inc))

(defmethod move :south
  [entity]
  (update-in entity [:position :y] dec))

(defmethod move :west
  [entity]
  (update-in entity [:position :x] inc))

(defmethod move :east
  [entity]
  (update-in entity [:position :x] dec))

(defmethod move :default [entity] entity)
(move player)
;; => {:name "Lea", :health 200, :position {:x 10, :y 11, :facing :north}}

(move {:position {:x 10 :y 10 :facing :west}})
;; => {:position {:x 11, :y 10, :facing :west}}

;;; activity 3.01
(def walking-speed 5) ;; kmh
(def driving-speed 70) ;; kmh

(def paris {:lat 48.856483, :lon 2.352413})
(def bordeaux {:lat 44.834999, :lon -0.575490})

(defn distance [from to]
  (let [{lat1 :lat lon1 :lon} from
        {lat2 :lat lon2 :lon} to
        cos-lat1 (Math/cos lat1)
        x-sq (Math/pow (- lat2 lat1) 2)
        y-sq (Math/pow (- lon2 lon1) 2)
        y-cos (* cos-lat1 y-sq)
        combined (Math/sqrt (+ x-sq y-cos))]
    (* 110.25 combined)))

(defmulti itinerary :transport)

(defmethod itinerary :walking
  [hmap]
  (let [from (:from hmap)
        to (:to hmap)
        dist (distance from to)
        duration (/ dist walking-speed)]
    {:cost 0, :distance dist, :duration duration}))

(def vehicle-cost-fns
  {:sporche (partial * 0.12 1.3)
   :tayato (partial * 0.07 1.3)
   :sleta (partial * 0.2 0.1)})

(defmethod itinerary :driving
  [{:keys [:from :to :vehicle]}]
  (let [driving-distance (distance from to)
        cost ((vehicle vehicle-cost-fns) driving-distance)
        duration (/ driving-distance driving-speed)]
    {:cost cost :distance driving-distance :duration duration}))

(def london {:lat 51.507351 :lon -0.127758})
(def manchester {:lat 53.480759 :lon -2.242631})

(itinerary {:from london :to manchester :transport :walking})
;; => {:cost 0, :distance 254.7501468129495, :duration 50.9500293625899}
;; => {:cost 0, :distance ##NaN, :duration ##NaN}
(itinerary {:from manchester :to london :transport :driving :vehicle :sleta})
;; => {:cost 5.095002936258991, :distance 254.7501468129495, :duration 3.6392878116135643}
