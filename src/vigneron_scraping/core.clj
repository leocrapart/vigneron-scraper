(ns vigneron-scraping.core
  (:gen-class)
  (:require [net.cgrand.enlive-html :as html]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))



;;;;;;;;;;; SCRAPE URLS

; suite : 
; scrape urls of pages => url list
; count 
; scrape (1 sec per page)

; fix page scraping for page-data with blanks : see page and upgrade alg

(def url-search "https://www.vigneron-independant.com/search-vigneron")

(def content-search
  (html/html-resource (java.net.URL. url-search)))

; #search-right > div:nth-child(2) > div.result-name > a
;  [:div (html/nth-child 2)] :div.result-name :a
(defn url [content]
  (first (:content (first 
      (html/select content [:body])))))

(url content-search)

content-search

; n : page-number (page-2, page-3, ...)
; x : post-number

(defn search-page [n]
  (html/html-resource (str "page-" n ".html")))

; #search-right > div:nth-child(2) > div.result-name > a

; boosted search-page fn
(defn x-page [n]
  (slurp (str "resources/page-" n ".html")))


; (defn url-x [n x]
;   (:href
;   (:attrs
;   (first
;   (html/select (search-page n) [:#search-right [:div (html/nth-child (+ 1 x))] :div.result-name :a])))))

; boosted

(defn url-x [n x]
  (let [string-x ((clojure.string/split (x-page n) #"\"result-name\"><a href=\"") x)]
    (first
      (clojure.string/split string-x #"\">"))))

; (url-x 58 1)

(def first-60-naturals (map inc (range 60)))

(defn url-function-for-page-2 [x]
  ((partial url-x 2) x))

(defn url-function-for-page-n [n x]
  ((partial url-x n) x))

(defn urls [n]
  (map (partial url-function-for-page-n n) first-60-naturals))


(defn inject-new-line [a b]
  (str a "\n" b))

(defn inject-new-lines [seq]
  (reduce inject-new-line seq))

(defn save-seq-into-txt-file [file-name seq]
  (spit file-name (inject-new-lines seq)))



(defn scrape-urls [n]
  (save-seq-into-txt-file (str "urls-" n ".txt") (urls n)))

; (scrape-urls 9)

; last one (98)
; modify first-60-naturals to the number of posts on last page
; and run this
; then put number back to 60 for first-60-naturals
; (save-seq-into-txt-file (str "urls-" 98 ".txt") (urls 98))
; (save-seq-into-txt-file (str "urls-" 0 ".txt") (urls 0))


; (drop 10 (map inc (range 100)))

(defn scrape-all-urls []
  (let [first-98 (map inc (range 98))
        from (drop 10 first-98)]
    (map scrape-urls from)))

(scrape-all-urls)




;;;;;;;; CONCATENATE

(def first-98-naturals
  (range 99))

(defn add-new-line [urls]
  (str urls "\n"))

(defn retrieve-urls [n]
  (add-new-line
    (slurp (str "urls-" n ".txt"))))

(defn all-urls-seq []
  (map retrieve-urls first-98-naturals))

(count (all-urls-seq))


(defn concatenate-txt []
  (save-seq-into-txt-file "urls.txt" (all-urls-seq)))

(concatenate-txt)






;;;;;;; READ

;;; get urls into a vector
; filter empty due to the new line in the text file for clarity
; at each 60 urls
(defn not-empty? [thing]
  (not (empty? thing)))
(not-empty? "")

(defn remove-empty-strings [vec]
  (filter not-empty? vec))

(def url-list 
  (remove-empty-strings
    (clojure.string/split (slurp "urls.txt") #"\n")))
url-list

(count url-list)





;;;;;;;;;; SCRAPE PAGE WITH URL

; stickit-Contact = :body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)]

(def sticky-contact
  [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)]])

#stickit-Contact
/html/body/section/div/div[3]/div/div/div/div/div[7]
/html/body/section/div/div[3]/div/div/div/div/div[8]

(def page-pic 
  (html/html-resource 
    (java.net.URL. "https://www.vigneron-independant.com/domaine-du-pic")))

(def jeune 
  (html/html-resource 
    (java.net.URL. "https://www.vigneron-independant.com/champagne-lejeune-pere-et-fils")))

(def delaunay 
  (html/html-resource 
    (java.net.URL. "https://www.vigneron-independant.com/domaine-delaunay-1")))

(def fontan 
  (html/html-resource 
    (java.net.URL. "https://www.vigneron-independant.com/vignobles-fontan")))

(defn domain-name [content]
  (first (:content
      (first 
        (html/select content [:body :section :div :div.panel-display.panel-1col.clearfix :div :div :div :h2])))))

(defn name-1 [content]
  (first (:content 
    (first 
      (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)] :div.establishment-contact :div :div.fn.org :div :div :div.field-item.even])))))

(defn name-2 [content]
  (first (:content 
      (second 
        (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)] [:div (html/nth-child 2)]])))))

(defn street-address [content]
  (second 
    (:content 
      (second 
        (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)] [:div (html/nth-child 2)] :div])))))

(defn postal-code [content]
  (first (:content
      (first 
        (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)] :div.establishment-contact :div :div.adr :span.postal-code])))))

(defn locality [content]
  (first (:content
      (first 
        (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)] :div.establishment-contact :div :div.adr :span.locality])))))

(defn mobile [content]
  (first (:content
      (first 
        (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)] :div.establishment-contact :div :div.tel :span])))))


; version 2 for https://www.vigneron-independant.com/domaine-du-pic

(defn street-address-2 [content]
  (second
    (:content
      (first
        (html/select content 
          (conj sticky-contact :div.establishment-contact :div :div.adr :div))))))

(defn postal-code-2 [content]
  (first (:content
      (first 
        (html/select content 
          (conj sticky-contact :div.establishment-contact :div :div.adr :span.postal-code))))))

(defn locality-2 [content]
  (first (:content
      (first 
        (html/select content 
          (conj sticky-contact :div.establishment-contact :div :div.adr :span.locality))))))

(defn name-2-2 [content]
  (first (:content
      (first 
        (html/select content 
          (conj sticky-contact :div.establishment-contact :div :div.fn.org :div :div :div))))))


(defn mobile-2 [content]
  (first (:content
      (first 
        (html/select content 
          (conj sticky-contact :div.establishment-contact :div [:div (html/nth-child 4)] :span))))))

(mobile-2 fontan)

(html/select fontan 
    (conj sticky-contact :div.establishment-contact :div [:div (html/nth-child 4)] :span))

(defn tel-2 [content]
  (first (:content
      (first 
        (html/select content 
          (conj sticky-contact :div.establishment-contact :div [:div (html/nth-child 3)] :span))))))

(defn fax-2 [content]
  (first (:content
      (first 
        (html/select content 
          (conj sticky-contact :div.establishment-contact :div [:div (html/nth-child 5)] :span))))))

(defn website-2 [content]
  (first (:content
      (first 
        (html/select content 
          (conj sticky-contact :div.establishment-contact :div :div.url :a))))))


(defn website [content]
  (first (:content
      (first (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 7)] :div.establishment-contact :div :div.url :a])))))

(def page-pic 
  (html/html-resource 
    (java.net.URL. "https://www.vigneron-independant.com/domaine-du-pic")))

(def jeune 
  (html/html-resource 
    (java.net.URL. "https://www.vigneron-independant.com/champagne-lejeune-pere-et-fils")))

(def delaunay 
  (html/html-resource 
    (java.net.URL. "https://www.vigneron-independant.com/domaine-delaunay-1")))

(def fontan 
  (html/html-resource 
    (java.net.URL. "https://www.vigneron-independant.com/vignobles-fontan")))


(name-2-2 page-pic)
(street-address-2 page-pic)
(postal-code-2 page-pic)
(locality-2 page-pic)
(mobile-2 page-pic)
(tel-2 page-pic)
(fax-2 page-pic)
(website-2 page-pic)

(def page delaunay)

(name-2-2 delaunay)
(street-address-2 page)
(postal-code-2 page)
(locality-2 page)
(mobile-2 page) 
(tel-2 page)
(fax-2 page)
(website-2 page)

(mobile delaunay)
(mobile-2 delaunay)
(mobile fontan)
(mobile-2 fontan)

(defn wine-name [content] 
  (first (:content (first 
      (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 8)] [:div (html/nth-child 1)] :div :div :div])))))

(defn wine-place [content] 
  (first (:content (second 
      (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 8)] [:div (html/nth-child 1)] :div :div :div])))))

(defn wine-designation [content]
  (first (:content (first 
      (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 8)] [:div (html/nth-child 1)] :div :div :div [:li (html/nth-child 1)]])))))

(defn wine-domain [content]
  (first (:content (first 
      (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 8)] [:div (html/nth-child 1)] :div :div :div [:li (html/nth-child 2)]])))))

(defn wine-type [content]
  (first (:content (first 
      (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 8)] [:div (html/nth-child 1)] :div :div :div [:li (html/nth-child 3)]])))))

(defn wine-color [content]
  (first (:content (first 
      (html/select content [:body :section :div [:div (html/nth-child 3)] :div :div :div :div [:div (html/nth-child 8)] [:div (html/nth-child 1)] :div :div :div [:li (html/nth-child 3)]])))))




(defn page-data [url]
  (let [content (html/html-resource (java.net.URL. url))]
    {:domain-name (domain-name content)
     :name-1 (name-1 content)
     :name-2 (name-2 content)
     :street-address (street-address content)
     :postal-code (postal-code content)
     :locality (locality content)
     :mobile (mobile content)
     :website (website content)
     :wine {:wine-name (wine-name content)
            :wine-place (wine-place content)
            :wine-designation (wine-designation content)
            :wine-domain (wine-domain content)
            :wine-type (wine-type content)
            :wine-color (wine-color content)}}))
(page-data url)


(defn type-is-str [thing]
  (if (= (type thing) (type ""))
    true
    false))

(defn nil-to-empty-str [thing]
  (if thing 
    ; if type is not string : empty str
    (if (type-is-str thing) 
      thing
      "")
    ""))

(defn excel-row [page-data]
  (vec 
    (map nil-to-empty-str
      [(page-data :domain-name)
       (page-data :name-1)
       (page-data :name-2)
       (page-data :street-address)
       (page-data :postal-code)
       (page-data :locality)
       (page-data :mobile)
       (page-data :website)
       (get-in page-data [:wine :wine-name])
       (get-in page-data [:wine :wine-place])
       (get-in page-data [:wine :wine-designation])
       (get-in page-data [:wine :wine-domain])
       (get-in page-data [:wine :wine-type])
       (get-in page-data [:wine :wine-color])
       ])))

(excel-row (page-data url))


;;;;;;;;;;;;;; SCRAPE PAGES WITH URLS

(def url-list-manual
  [ ; page 1
   "https://www.vigneron-independant.com/champagne-lejeune-pere-et-fils"
   "https://www.vigneron-independant.com/domaine-du-val-de-gilly"
   "https://www.vigneron-independant.com/chateau-des-correges"
   "https://www.vigneron-independant.com/vignobles-tourrel"
   "https://www.vigneron-independant.com/domaine-cartaux-bougaud"
   "https://www.vigneron-independant.com/domaine-dugois"
   "https://www.vigneron-independant.com/domaine-delaunay-0"
   "https://www.vigneron-independant.com/musculus-claude"
   "https://www.vigneron-independant.com/domaine-du-pic"
   ; page 2
   "https://www.vigneron-independant.com/domaine-thomas-labaille"
   "https://www.vigneron-independant.com/chateau-les-hauts-daglan"
   "https://www.vigneron-independant.com/chateau-la-bretonniere"
   "https://www.vigneron-independant.com/champagne-jean-yves-de-carlini"
   "https://www.vigneron-independant.com/domaine-turenne"
   "https://www.vigneron-independant.com/noulens-christian"
   "https://www.vigneron-independant.com/chateau-sainte-roseline"
   "https://www.vigneron-independant.com/domaine-de-valette"
   "https://www.vigneron-independant.com/domaine-du-moura"
   "https://www.vigneron-independant.com/chateau-les-crostes"
   "https://www.vigneron-independant.com/chateau-la-martinette"
  ])

(count url-list)


; scrapes the pages
(defn data [n]
  (vec
    (map page-data (take n url-list))))

(data 10)
(data 20)
(data 30)
(data 40)
(data 50)
(count (data 20))

(def title-list 
  ["domain-name" "name-1" "name-2" "street-address" "postal-code" "locality" "mobile" "website" "wine-name" "wine-place" "wine-designation" "wine-domain" "wine-type" "wine-color"])

(defn for-excel [data]
  `[~title-list ~@(vec (map excel-row data))])

; (count (for-excel data))

; to excel
(use 'dk.ative.docjure.spreadsheet)
(defn save-excel [data]
  ;; Create a spreadsheet and save it
  (let [wb (create-workbook "vigneron data"
                            ; [["domain-name" "name-1" "name-2" "street-address" "postal-code" "locality" "mobile" "website" "wine-name" "wine-place" "wine-designation" "wine-domain" "wine-type" "wine-color"]
                            ;  (excel-row (data 0))
                            ;  (excel-row (data 1))
                            ;  ]
                            (for-excel data)
                            )
        sheet (select-sheet "vigneron data" wb)
        header-row (first (row-seq sheet))]
    (set-row-style! header-row (create-cell-style! wb {:background :yellow,
                                                       :font {:bold true}}))
    (save-workbook! "scraped-data.xlsx" wb)))

(defn scrape-and-save-excel []
  (let [data (data 500)]
    (save-excel data)))

(scrape-and-save-excel)

;;;; test zone
