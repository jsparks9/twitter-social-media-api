package com.cooksys.social_network_api.config;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The names were generated in Python with the package "names"
 */

public class Data {
    private final String[] names;
    private final String[] emails;
    private final String[] genTweet;
    private final String[] radioStationTag;
    private final String[] exoticLocationTag;
    private final String[] actionTag;
    private final String[] foodTag;
    private final String[] animalTag;
    private final String[] countryTag;
    private final String[] looksTag;
    private final String[] activityTag;
    private final String[] aboutTag;

    private static final String genNames  = """
            Thomas Stewart
            Rebekah Harpin
            Thomas Page
            Wanda Maynard
            Amber Elias
            Hugh Dugan
            Linda Kelley
            William Quinones
            Marjorie Bunker
            Charles Cullen
            Lester Campanile
            Katie Moser
            Jay Marshall
            Kelly Johnson
            Helen Smith
            Bernadette Martinez
            Ruth Collier
            Jeremiah Miller
            Erica Glass
            Julienne Eike
            Jenine Graves
            Marie Douglas
            Lawrence Thomas
            Donald Johnson
            James Kamal
            Viola Anaya
            Elizabeth Osler
            John Sears
            Shantay Robinson
            Melinda Surano
            Lois Burwell
            Ronald Crandall
            Gladys Simmons
            Carl Rivers
            Mary Jacoby
            Guadalupe Hartley
            Raymond Perry
            Donna Felton
            Becky Howerton
            Barbara Escovedo
            August Koonce
            Frankie Welty
            Rosemary Henshaw
            Julia Washington
            Mavis Nelson
            Kristy Huneycutt
            Mario Castrellon
            Cornelius Brodie
            Walter Dickerson
            Charlotte Hartman
            Rebecca Monroe
            Elva Sloan
            Robert Pena
            Joshua Brooks
            Alexandra Richey
            Thomas Savage
            Robert Adam
            Charles Hofmann
            Manuel Mattison
            Billy Bonilla
            Michael Perry
            William Ordonez
            Robert Hales
            Garry Kahrs
            James Ramos
            Daniel Searcy
            Leon Moulton
            Tina Levy
            Becky Knox
            Tiffany Lee
            Gilbert Schut
            Patricia Hill
            Dawn Berube
            Dana Loomis
            Maria Fowler
            Anthony Fletcher
            Tom Sahsman
            Eric Searles
            Thomas Whitely
            Juliana Brothern
            Rocio Palmer
            Charles Escobar
            Stephen Hawkins
            Amanda Moats
            Ernest Watts
            Samuel Bennett
            Barbara Orcutt
            Darius Peachey
            Ray Oldaker
            Hector Chew
            Paul Doyle
            Margaret Boyle
            Dan Harold
            Martin Fitzgerald
            Larry Allen
            Patrick Molinaro
            John Buckner
            Tonya Pierce
            Robert Halstead
            Alison Pennel
            """;
    private static final String genEmails = """
            yumpy@live.com
            rhavyn@mac.com
            xtang@msn.com
            mpiotr@yahoo.ca
            pjacklam@att.net
            bryanw@verizon.net
            goldberg@yahoo.ca
            aprakash@sbcglobal.net
            milton@msn.com
            atmarks@outlook.com
            webteam@gmail.com
            joglo@msn.com
            dawnsong@att.net
            jkegl@att.net
            chaffar@yahoo.ca
            jfriedl@live.com
            nachbaur@live.com
            panolex@yahoo.ca
            fraterk@icloud.com
            kingjoshi@optonline.net
            wonderkid@icloud.com
            nullchar@aol.com
            schwaang@att.net
            leocharre@outlook.com
            gastown@att.net
            nichoj@yahoo.com
            goresky@outlook.com
            hamilton@optonline.net
            bester@comcast.net
            mwilson@icloud.com
            fangorn@aol.com
            hermanab@comcast.net
            kmself@yahoo.ca
            ismail@me.com
            rwelty@me.com
            dougj@verizon.net
            nullchar@sbcglobal.net
            nick@optonline.net
            sbmrjbr@gmail.com
            amcuri@yahoo.ca
            gregh@yahoo.com
            pierce@yahoo.com
            enintend@gmail.com
            tarreau@mac.com
            seano@yahoo.com
            clkao@att.net
            nichoj@gmail.com
            jramio@aol.com
            mbrown@icloud.com
            gemmell@icloud.com
            hauma@me.com
            earmstro@comcast.net
            kudra@sbcglobal.net
            markjugg@mac.com
            matthijs@yahoo.ca
            damian@live.com
            webinc@hotmail.com
            unreal@aol.com
            lahvak@gmail.com
            burns@icloud.com
            marin@icloud.com
            caidaperl@optonline.net
            starstuff@outlook.com
            jdray@icloud.com
            shazow@hotmail.com
            jacks@comcast.net
            kidehen@gmail.com
            stern@aol.com
            grady@mac.com
            hoangle@gmail.com
            irving@icloud.com
            nighthawk@hotmail.com
            satch@me.com
            penna@optonline.net
            denton@verizon.net
            lauronen@msn.com
            adhere@aol.com
            jyoliver@att.net
            rohitm@mac.com
            kspiteri@optonline.net
            kingjoshi@me.com
            zwood@icloud.com
            mpiotr@hotmail.com
            fhirsch@yahoo.com
            jgmyers@yahoo.com
            goresky@optonline.net
            jbearp@icloud.com
            loscar@me.com
            kjohnson@hotmail.com
            novanet@sbcglobal.net
            arnold@gmail.com
            gilmoure@att.net
            emmanuel@aol.com
            kudra@yahoo.ca
            trieuvan@yahoo.com
            dsowsy@aol.com
            dmath@optonline.net
            koudas@optonline.net
            carcus@verizon.net
            mcnihil@outlook.com
            """;
    private static final String activityTags = """
            antiqueCrafting
            baking
            cooking
            drawing
            gaming
            guitar
            mushroom
            piano
            poetry
            sailing
            scuba
            skateboarding
            spearfishing
            theater
            webBrowsing
            """;
    private static final String actionTags = """
            annihilate
            attack
            behead
            bellyflop
            cremate
            eat
            electrocute
            grab
            maim
            manhandle
            maul
            nab
            nail
            pinch
            scratch
            shatter
            shred
            slay
            vomit
            """;
    private static final String radioStationTags = """
            balearicBeat
            bouyon
            brazilian
            compas
            instrumentalCountry
            KansasCityJazz
            melodicDeathMetal
            newJerseyHipHop
            originalPilipinoMusic
            powerviolence
            rebetiko
            timba
            """;
    private static final String exoticLocationTags = """
            Algeria
            Barbados
            Bhutan
            Canada
            Grenada
            JuanDeNovaIsland
            Kazakhstan
            Macau
            Moldova
            Nauru
            NewZealand
            SaintLucia
            SaoTomeAndPrincipe
            SpratlyIslands
            Tanzania
            TheGambia
            """;
    private static final String foodTags = """
            bonbon
            meatball
            truffle
            TurkishDelight
            TurkishDelight
            coconut
            sweetmeat
            bonbon
            jellybean
            fudge
            apricot
            nectarine
            bun
            onion
            biscuit
            strawberry
            peanut
            cheeseburger
            potato
            watermelon
            truffle
            hazelnut
            bonbon
            """;
    private static final String locationTag = """
            Alaska
            Argentina
            Azerbaijan
            Bahrain
            Bolivia
            Finland
            JuandeNovaIsland
            Kuwait
            Malaysia
            Martinique
            Mauritania
            Nepal
            NewCaledonia
            Norway
            Rwanda
            Samoa
            Singapore
            SouthKorea
            """;
    private static final String looksTags = """
            bearded
            brown
            colorful
            crooked
            dry
            floppy
            fluffy
            fuzzy
            gigantic
            humongous
            microscopic
            monochromatic
            narrow
            purple
            ragged
            red
            shriveled
            smoky
            soapy
            sopping
            spotless
            stout
            tall
            towering
            transparent
            uneven
            white
            whopping
            wide
            """;
    private static final String professionTags = """
            bartender
            busboy
            butler
            circusPerformer
            fireman
            fortune
            gambler
            gardener
            gasman
            hobbit
            salesman
            scribe
            taxidermist
            teacher
            """;
    private static final String aboutTags = """
            anticipation
            bliss
            contempt
            contentment
            fury
            hatred
            irritation
            jank
            optimism
            pleasure
            remorse
            sadness
            surprise
            """;
    private static final String petTags = """
            ant
            tiger
            dog
            gentooPenguin
            goldfinch
            hummingbird
            macaroniPenguin
            peacock
            penguin
            raven
            terrier
            tiger
            velociraptor
            vulture
            """;
    private static final String genTweets = """
            heard about this on #{radioStationTag} radio, decided to give it a try.
            I saw one of these in #{exoticLocationTag} and I bought one.
            I tried to #{actionTag} it but got #{foodTag} all over it.
            My #{animalTag} loves to play with it.
            It only works when I'm in #{countryTag}.
            My co-worker @{userMention} has one of these. He says it looks #{looksTag}.
            My neighbor @{userMention} has one of these. She works as a #{professionTag} and she says it looks #{looksTag}.
            one of my hobbies is #{activityTag}. and when i'm #{activityTag} this works great.
            SoCal #{foodTag} is #{looksTag}, #{looksTag}, and tenacious.
            SoCal #{foodTag} are #{looksTag}, #{looksTag}, and tenacious.
            talk about #{aboutTag}!
            talk about #{aboutTag}!!
            talk about #{aboutTag}!!!
            talk about #{aboutTag}.
            It only works when the lights are off.
            I saw this on TV and wanted to give it a try.
            works okay.
            """;

    public Data() {

        this.names = genNames.split("\n"); // sets this.names to an array with each entry like "first last"
        String[] emailBuilder = genEmails.split("\n"); // this becomes an array with each entry like "abc@domain.com"

        for (int i=0; i < emailBuilder.length; i++ ) { // change 40% of the emails to first.last@domain.com
            if(ThreadLocalRandom.current().nextInt(0, 10) < 4) {
                emailBuilder[i] = this.names[i].replace(" ",".") +
                        "@" + emailBuilder[i].split("@")[1];
            }
        }
        this.emails = emailBuilder; // sets this.emails to the final list of emails

        this.genTweet = genTweets.split("\n");
        this.radioStationTag = radioStationTags.split("\n");
        this.exoticLocationTag = exoticLocationTags.split("\n");
        this.actionTag = actionTags.split("\n");
        this.foodTag = foodTags.split("\n");
        this.animalTag = petTags.split("\n");
        this.countryTag = locationTag.split("\n");
        this.looksTag = looksTags.split("\n");
        this.activityTag = activityTags.split("\n");
        this.aboutTag = aboutTags.split("\n");
    }

    // Getters
    public String[] getNames() { return names; }
    public String[] getEmails() { return emails; }
    public String[] getGenTweet() { return genTweet; }
    public String[] getRadioStationTag() { return radioStationTag; }
    public String[] getExoticLocationTag() { return exoticLocationTag; }
    public String[] getActionTag() { return actionTag; }
    public String[] getFoodTag() { return foodTag; }
    public String[] getAnimalTag() { return animalTag; }
    public String[] getCountryTag() { return countryTag; }
    public String[] getLooksTag() { return looksTag; }
    public String[] getActivityTag() { return activityTag; }
    public String[] getAboutTag() { return aboutTag; }
}
