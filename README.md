# IR-Search-Engine
## Team Members:
- Kaibin Yin
- Kehu Wu
- Xuliang Qin

## Setup
Use maven to import all dependencies or manually add library
Package Needed:
Lucene-core 4.7.2
lucene-queryparser 4.7.2
lucene-analyzers-common 4.7.2
jackson-databind 2.6.3

## Run
Use IDE to open IR-Search-Engine
Run \IR-Search-Engine\src\main\java\IR\Main.java
* Main Command UI:
```
Welcome to use our information retrieval system!
 1. Indexing and Retrieval
 2. Displaying Results
 3. Evaluation
 q. Exit
```
At any time, you can enter "q" to quit current Command UI.

### Phase 1:
Input 1 in **Main Command UI** , direct you to following instructions:
```
Do you want to update corpus for Phase 1, task 1(y/n) :

```
If you input "y", corpus for phase 1, task 1 will store in 
\IR-Search-Engine\src\main\resources\Corpus
```
Based on corpus with Casefold and Punctuation!
Do you want to update unigram and language models for Phase 1, task 1(y/n) :
```
If you input "y", unigram for phase 1, task 1 will store in 
\IR-Search-Engine\src\main\resources\task1_unigram.txt

* Phase 1 Command UI:
```
Phase 1 Instruction：
 1. Update BM25 Query Table
 2. Update TF IDF Query table 
 3. Update Smoothed Query Likelihood Model table 
 4. Update Lucene Query table
 5. Update result for 3 base runs with Stop 
 6. Update result for 3 base runs with Stem 
 q. Quit
 ```

#### Task 1 && Task 2:
1. Input 1 in **Phase 1 Command UI**, direct you to following instructions:
```

Perform Pseudo-Relevance Feedback?(y/n):
```
Input "y", will store result for task 2 : PRF for BM 25 in 
\IR-Search-Engine\src\main\resources\task2\BM25Query. 
Otherwise, will only store result for task 1 : BM25 in 
\IR-Search-Engine\src\main\resources\task1\BM25Query

2. Input 2 in **Phase 1 Command UI**, direct you to following instructions:
```

Perform Pseudo-Relevance Feedback?(y/n):
```
Input "y", will store result for task 2 : PRF for BM 25 in 
\IR-Search-Engine\src\main\resources\task2\TfIdfQuery. 
Otherwise, will only store result for task 1 : BM25 in 
\IR-Search-Engine\src\main\resources\task1\TfIdfQuery

3. Input 3 in **Phase 1 Command UI**, will store result for task 1 : Smoothed Query Likelihood Model in 
\IR-Search-Engine\src\main\resources\task1\LikelihoodQuery

4. Input 4 in **Phase 1 Command UI**, will store result for task 1 : Lucene in 
\IR-Search-Engine\src\main\resources\task1\LuceneQuery

#### Task 3:
1. Input 5 in **Phase 1 Command UI**, direct you to following instructions:
```

Do you want to update corpus for Phase 1, task 3(y/n) :
```
Input "y", corpus with Stopping will store in 
\IR-Search-Engine\src\main\resources\Task3Corpus
```
Based on corpus with Stop, Casefold and Punctuation!
Do you want to update unigram and language models for Phase 1, task 3 A(y/n) :
```
If you input "y", unigram for phase 1, task 1 will store in 
\IR-Search-Engine\src\main\resources\task#A_unigram.txt
And search result will store in
P:\Development\Git\IR-Search-Engine\src\main\resources\task3\

2. Input 6 in **Phase 1 Command UI**, direct you to following instructions:
```

Do you want to update unigram and language models for Phase 1, task 3 B(y/n) :
```
If you input "y", unigram for phase 1, task 3B will store in 
\IR-Search-Engine\src\main\resources\task3B_unigram.txt
```
Based on corpus with Stop, Casefold and Punctuation!
Do you want to update unigram for Phase 1, task 3 A(y/n) :
```
And search result will store in
P:\Development\Git\IR-Search-Engine\src\main\resources\task3\

### Phase 2:
Input 2 in **Main Command UI** , direct you to following instructions:
```
Phase 2 Instruction:
Please input the query ID to get the search result, (0 to output all result, q : quit)

```
1. If you input "0",
```
Choose the retrieval model the search used:
 1. BM25 2. TF-IDF 3. Smoothed Query Likelihood Model 4. Lucene

```
You can input 1 - 4 to generate highlight for query, and store path will output on screen.
2. You can input specified query id, such as "1":
```
Choose the retrieval model the search used:
 1. BM25 2. TF-IDF 3. Smoothed Query Likelihood Model 4. Lucene
You can input 1 - 4 to generate highlight for query, and store path will output on screen.
```

### Phase 3:
Input 3 in **Main Command UI** , direct you to following instructions:
```
Notice : You are using corpus generating in Indexing and Retrieval
Phase 3 Instruction：
 1. Update evaluation for BM25 Query
 2. Update evaluation for TF IDF Query table 
 3. Update evaluation for Likelihood Model table 
 4. Update evaluation for Lucene Query table
 5. Update evaluation for BM25 Pseudo-Relevance Query table
 6. Update evaluation for TF IDF Pseudo-Relevance Query table
 7. Update evaluation for BM25 with stop
 8. Update evaluation for TF IDF with stop
 9. Update evaluation for Likelihood with stop
 q. Quit
 ```
You can input 1 - 9 to generate evaluation for corresponding retrieve model, and result path will out put on screen.

### Phase 3:
Input 4 in **Main Command UI** , direct you to following instructions:
```
Do you want to Stopping the corpus? Extract_Credit(y/n) :
q for exit :
 ```
You can input y or n to generate Stopping result or NonStopping result.
