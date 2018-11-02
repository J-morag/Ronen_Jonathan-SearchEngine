package Indexing;

import Elements.Document;
import Elements.TermDocument;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;

class ParseTest {

    ArrayBlockingQueue<Document> docs = new ArrayBlockingQueue<Document>(10);
    ArrayBlockingQueue<TermDocument> termDocs = new ArrayBlockingQueue<TermDocument>(10);

    @Test
    void parseConcurrent() {

        Parse p = new Parse("C:\\Users\\John\\Google Drive\\Documents\\1Uni\\Semester E\\information retrieval 37214406\\Assignements\\Ass1",
                docs, termDocs);
        Document doc1 = new Document();
        doc1.setHeader("Alice's Adventures in Wonderland");
        doc1.setDocId("Alice01");
        doc1.setText(alice);
        Document doc2 = new Document();
        doc2.setHeader("Technical Document Test");
        doc2.setDocId("Tech01");
        doc2.setText(technicalDocument);

        Thread parser1 = new Thread(p);
        parser1.start();
//        Thread parser2 = new Thread(p);
//        parser2.start();
//        Thread parser3 = new Thread(p);
//        parser3.start();


        Thread dummyConsumer = new Thread(() -> {
            try {
                boolean done1=false, done2=true, done3=true;
                while(!done1 || !done2 || !done3){
                    if(termDocs.take().getDocId() == null){
                        if(!done1) done1 = true;
//                        else if(!done2) done2 = true;
//                        else done3 = true;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        dummyConsumer.start();

        long startTime = System.currentTimeMillis();

        try {
            int i =0;
            while (i<1000) {
                docs.put(doc1);
                docs.put(doc2);
                i++;
            }
            Document poison = new Document();
            poison.setText(null);
            docs.put(poison);
//            docs.put(poison);
//            docs.put(poison);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            dummyConsumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(System.currentTimeMillis() - startTime);

    }

    @Test
    void parseSerialized(){
        Parse p = new Parse("C:\\Users\\John\\Google Drive\\Documents\\1Uni\\Semester E\\information retrieval 37214406\\Assignements\\Ass1",
                docs, termDocs);
        Parse.debug = true;
        Document doc1 = new Document();
        doc1.setHeader("Test Cases for Parsing");
        doc1.setDocId("testCases");
        doc1.setText(testCases);

        long startTime = System.currentTimeMillis();

        p.parseOneDocument(doc1);

//        System.out.println(System.currentTimeMillis() - startTime);
    }


    static final String alice = "Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do: once or twice she had peeped into the book her sister was reading, but it had no pictures or conversations in it, `and what is the use of a book,' thought Alice `without pictures or conversation?'\n" +
            "\n" +
            "So she was considering in her own mind (as well as she could, for the hot day made her feel very sleepy and stupid), whether the pleasure of making a daisy-chain would be worth the trouble of getting up and picking the daisies, when suddenly a White Rabbit with pink eyes ran close by her.\n" +
            "\n" +
            "There was nothing so very remarkable in that; nor did Alice think it so very much out of the way to hear the Rabbit say to itself, `Oh dear! Oh dear! I shall be late!' (when she thought it over afterwards, it occurred to her that she ought to have wondered at this, but at the time it all seemed quite natural); but when the Rabbit actually took a watch out of its waistcoat-pocket, and looked at it, and then hurried on, Alice started to her feet, for it flashed across her mind that she had never before seen a rabbit with either a waistcoat-pocket, or a watch to take out of it, and burning with curiosity, she ran across the field after it, and fortunately was just in time to see it pop down a large rabbit-hole under the hedge.  In another moment down went Alice after it, never once considering how in the world she was to get out again.\n" +
            "\n" +
            "The rabbit-hole went straight on like a tunnel for some way, and then dipped suddenly down, so suddenly that Alice had not a moment to think about stopping herself before she found herself falling down a very deep well.\n" +
            "\n" +
            "Either the well was very deep, or she fell very slowly, for she had plenty of time as she went down to look about her and to wonder what was going to happen next. First, she tried to look down and make out what she was coming to, but it was too dark to see anything; then she looked at the sides of the well, and noticed that they were filled with cupboards and book-shelves; here and there she saw maps and pictures hung upon pegs. She took down a jar from one of the shelves as she passed; it was labelled `ORANGE MARMALADE', but to her great disappointment it was empty: she did not like to drop the jar for fear of killing somebody, so managed to put it into one of the cupboards as she fell past it.\n" +
            "\n" +
            "`Well!' thought Alice to herself, `after such a fall as this, I shall think nothing of tumbling down stairs! How brave they'll all think me at home! Why, I wouldn't say anything about it, even if I fell off the top of the house!' (Which was very likely true.)\n" +
            "\n" +
            "Down, down, down. Would the fall never come to an end! `I wonder how many miles I've fallen by this time?' she said aloud. `I must be getting somewhere near the centre of the earth. Let me see: that would be four thousand miles down, I think--' (for, you see, Alice had learnt several things of this sort in her lessons in the schoolroom, and though this was not a very good opportunity for showing off her knowledge, as there was no one to listen to her, still it was good practice to say it over) `--yes, that's about the right distance--but then I wonder what Latitude or Longitude I've got to?' (Alice had no idea what Latitude was, or Longitude either, but thought they were nice grand words to say.)\n" +
            "\n" +
            "Presently she began again. `I wonder if I shall fall right through the earth! How funny it'll seem to come out among the people that walk with their heads downward! The Antipathies, I think--' (she was rather glad there was no one listening, this time, as it didn't sound at all the right word) `--but I shall have to ask them what the name of the country is, you know. Please, Ma'am, is this New Zealand or Australia?' (and she tried to curtsey as she spoke--fancy curtseying as you're falling through the air! Do you think you could manage it?) `And what an ignorant little girl she'll think me for asking! No, it'll never do to ask: perhaps I shall see it written up somewhere.'\n" +
            "\n" +
            "Down, down, down. There was nothing else to do, so Alice soon began talking again. `Dinah'll miss me very much to-night, I should think!' (Dinah was the cat.) `I hope they'll remember her saucer of milk at tea-time. Dinah my dear! I wish you were down here with me! There are no mice in the air, I'm afraid, but you might catch a bat, and that's very like a mouse, you know. But do cats eat bats, I wonder?' And here Alice began to get rather sleepy, and went on saying to herself, in a dreamy sort of way, `Do cats eat bats? Do cats eat bats?' and sometimes, `Do bats eat cats?' for, you see, as she couldn't answer either question, it didn't much matter which way she put it. She felt that she was dozing off, and had just begun to dream that she was walking hand in hand with Dinah, and saying to her very earnestly, `Now, Dinah, tell me the truth: did you ever eat a bat?' when suddenly, thump! thump! down she came upon a heap of sticks and dry leaves, and the fall was over.\n" +
            "\n" +
            "Alice was not a bit hurt, and she jumped up on to her feet in a moment: she looked up, but it was all dark overhead; before her was another long passage, and the White Rabbit was still in sight, hurrying down it. There was not a moment to be lost: away went Alice like the wind, and was just in time to hear it say, as it turned a corner, `Oh my ears and whiskers, how late it's getting!' She was close behind it when she turned the corner, but the Rabbit was no longer to be seen: she found herself in a long, low hall, which was lit up by a row of lamps hanging from the roof.\n" +
            "\n" +
            "There were doors all round the hall, but they were all locked; and when Alice had been all the way down one side and up the other, trying every door, she walked sadly down the middle, wondering how she was ever to get out again.\n" +
            "\n" +
            "Suddenly she came upon a little three-legged table, all made of solid glass; there was nothing on it except a tiny golden key, and Alice's first thought was that it might belong to one of the doors of the hall; but, alas! either the locks were too large, or the key was too small, but at any rate it would not open any of them. However, on the second time round, she came upon a low curtain she had not noticed before, and behind it was a little door about fifteen inches high: she tried the little golden key in the lock, and to her great delight it fitted!  " +
            "Alice opened the door and found that it led into a small passage, not much larger than a rat-hole: she knelt down and looked along the passage into the loveliest garden you ever saw. How she longed to get out of that dark hall, and wander about among those beds of bright flowers and those cool fountains, but she could not even get her head though the doorway; `and even if my head would go through,' thought poor Alice, `it would be of very little use without my shoulders. Oh, how I wish I could shut up like a telescope! I think I could, if I only know how to begin.' For, you see, so many out-of-the-way things had happened lately, that Alice had begun to think that very few things indeed were really impossible.\n" +
            "\n" +
            "There seemed to be no use in waiting by the little door, so she went back to the table, half hoping she might find another key on it, or at any rate a book of rules for shutting people up like telescopes: this time she found a little bottle on it, (`which certainly was not here before,' said Alice,) and round the neck of the bottle was a paper label, with the words `DRINK ME' beautifully printed on it in large letters. ";


    static final String technicalDocument = "The second session of the eighth Heilongjiang \n" +
            "Provincial people's congress ended in Harbin this afternoon \n" +
            "after successfully fulfilling all items on the agenda. The \n" +
            "closing ceremony was presided over by Executive Chairman Sun \n" +
            "Weiben. Other executive chairmen of the congress, including Li \n" +
            "Genshen, An Zhendong, Qi Guiyuan, Xie Yong, Du Xianzhong, Liu \n" +
            "Hanwu, Liu Tongnian, (Zhang Shuyu), Wang Zhenlin, Liu Molin, \n" +
            "(Zhao Hongyan), and Wang Shouye, sat in the front row of the \n" +
            "rostrum. \n" +
            "  Leading comrades of the provincial, government and army \n" +
            "organs, including Shao Qihui, Zhou Wenhua, Ma Guoliang, Tian \n" +
            "Fengshan, Shan Rongfan, Meng Qingxiang, Yu Jingchang, Yang \n" +
            "Zhihai, Ma Shujie, Wang Haiyan, Huang Feng, Fu Shiying, Guo \n" +
            "Shouchang, Zhao Shijie, Chen Zhanyuan, Wang Zhitian, and Wu \n" +
            "Dinghe were also seated on the rostrum. Also sitting on the \n" +
            "rostrum were provincial-level retired veteran comrades, such as \n" +
            "Zhao Dezun, Chen Lei, Li Jianbai, Wang Zhao, and Chen Jianfei; \n" +
            "Tang Xianqiang, president of the provincial Higher People's \n" +
            "Court; and Yu Wanling, chief procurator of the provincial \n" +
            "People's Procuratorate. \n" +
            "  Voting by a show of hands, the congress session adopted the \n" +
            "method for electing members of the standing committee of the \n" +
            "eighth provincial people's congress, the namelist of chief \n" +
            "ballot supervisor and vice supervisors, the resolution of the \n" +
            "second session of the eighth Heilongjiang Provincial people's \n" +
            "congress on the government work report, the resolution of the \n" +
            "second session of the eighth Heilongjiang Provincial people's \n" +
            "congress on approving the report on the fulfillment of the 1993 \n" +
            "budget and on the 1994 draft budget, the resolution of the \n" +
            "second session of the eighth Heilongjiang Provincial people's \n" +
            "congress on the work report of the provincial people's congress \n" +
            "standing committee, the resolution of the second session of the \n" +
            "eighth Heilongjiang Provincial people's congress on the work \n" +
            "report of the provincial Higher People's Court, the resolution \n" +
            "of the second session of the eighth Heilongjiang Provincial \n" +
            "people's congress on the work report of the provincial People's \n" +
            "Procuratorate, the provisional regulation of the special \n" +
            "committee of the Heilongjiang Provincial people's congress, and \n" +
            "the decision on assigning the vice chairman of the standing \n" +
            "committee to concurrently hold the post as chairman of the \n" +
            "special committee of the eighth Heilongjiang Provincial people's \n" +
            "congress. \n" +
            "  Voting by secret ballot, 540 provincial deputies elected Xu \n" +
            "Wenzheng and Liang Weiling members of the eighth provincial \n" +
            "people's congress standing committee at the session. Amidst \n" +
            "enthusiastic applause, Sun Weiben presented certificates of \n" +
            "approval to the newly elected members of the provincial people's \n" +
            "congress standing committee and gave a speech. \n" +
            "  Sun Weiben said: The second session of the eighth provincial \n" +
            "people's congress has come to a successful end. It is hoped \n" +
            "that, after this congress session, all deputies will actively \n" +
            "publicize the congress guidelines, take the lead in implementing \n" +
            "all resolutions adopted at the congress, unite with the people \n" +
            "of all nationalities across the province, work with one heart \n" +
            "and one mind, immerse in hard work, and strive to realize our \n" +
            "established objective and promote a sustained, rapid, and sound \n" +
            "economic development throughout the whole province. \n" +
            "  The congress session ended with the solemn playing of the \n" +
            "national anthem. \n";

    static final String testCases =
            "1,000,000 Dollars 1 M Dollars\n" +
                    "$450,000,000\n" +
                    "450 M Dollars\n" +
                    "$100 million 100 M Dollars\n" +
                    "20.6m Dollars\n" +
                    "20.6 M Dollars\n" +
                    "$100 billion, 100000 M Dollars \n" +
                    "100bn Dollars, 100000 M Dollars\n" +
                    "100 billion U.S. dollars. 100000 M Dollars\n" +
                    "320 million U.S. dollars. 320 M Dollars\n" +
                    "1 trillion U.S. dollars. 1000000 M Dollars " +

                    "1.7320 Dollars: 1.7320 Dollars\n" +
                    "22 3/4 Dollars: 22 3/4 Dollars\n" +
                    "$450,000: 450,000 Dollars " +

                    " 6%\n" +
                    "10.6 percent    10.6%\n" +
                    "10.6 percentage     10.6%\n" +

                    "10,123 10.123K\n" +
                    "123 Thousand 123.456K\n" +
                    "1010.56 1.01056K\n" +

                    "10,123,000 10.123M\n" +
                    "55 Million 55M\n" +
                    ".56 1.01056K\n" +

                    "10,123,000,000 10.123B\n" +
                    "55 Billion 55B\n" +
                    "7 Trillion 700B";

}