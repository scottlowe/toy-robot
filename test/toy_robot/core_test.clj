(ns toy-robot.core-test
  (:use clojure.test
        toy-robot.core))

(def input-a
  [{:cmd :place
    :x 0
    :y 0
    :direction :north}
   {:cmd :move}
   {:cmd :report}])

(def input-b
  [{:cmd :place
    :x 0
    :y 0
    :direction :north}
   {:cmd :left}
   {:cmd :report}])

(def input-c
  [{:cmd :place
    :x 1
    :y 2
    :direction :east}
   {:cmd :move}
   {:cmd :move}
   {:cmd :left}
   {:cmd :move}
   {:cmd :report}])

(deftest exec-transfroms-input-a
  (is (= (exec input-a)
         {:x 0 :y 1
          :direction :north})))

(deftest exec-transfroms-input-b
  (is (= (exec input-b)
         {:x 0 :y 0
          :direction :west})))

(deftest exec-transfroms-input-c
  (is (= (exec input-c)
         {:x 3 :y 3
          :direction :north})))

(deftest robot-cannot-fall-off-table
  (let [actual-pos   (exec [{:cmd :place
                             :x (first table)
                             :y (last table)
                             :direction :east}
                            {:cmd :move}])
        expected-pos {:x (first table)
                      :y (last table)
                      :direction :east}]
    (is (= actual-pos expected-pos))))

(deftest can-match-place-cmd
    (is (= (place-match "") '()))
    (is (= (place-match "aaaaaa bbb ccc") '()))
    (is (= (place-match "PLACE a,b,NORTH") '()))
    (is (= (place-match "PLACE 0,NORTH") '()))
    (is (= (place-match "PLACE") '()))
    (is (= (place-match "PLACE 0,0,NORTH")
           ["PLACE" "0" "0" "NORTH"])))

(deftest can-match-cmd
  (is (= (cmd-match "") nil))
  (is (= (cmd-match "aaaaaa bbbb") nil))
  (is (= (cmd-match "PLACE") nil))
  (is (= (cmd-match "MOVE") "MOVE")))

(deftest parses-input-line-with-place
  (let [expected {:cmd       :place
                  :direction :north
                  :x         0
                  :y         0}]
    (is (= (parse-line "PLACE 0,0,NORTH")
           expected))))

(deftest parses-input-line-with-cmd
  (is (= (parse-line "MOVE")
         {:cmd :move})))