import java.util.Arrays;
import java.util.Scanner;

public class OOPAssignment {
    public static void main(String[] args) {
        //게임 참가 인원 설정
        System.out.print("게임에 참가할 플레이어수를 설정하세요.(정원: 최소 2명, 최대 4명)> ");
        Scanner scanner = new Scanner(System.in);
        int playernum = scanner.nextInt();
        while (!(1 < playernum && playernum <= 4)) {
            System.out.print("플레이어가 너무 적거나, 많습니다, 재설정 해주세요.(정원: 최소 2명, 최대 4명)> ");
            scanner = new Scanner(System.in);
            playernum = scanner.nextInt();
        }
        System.out.println("플레이어: " + playernum + "명");
        System.out.println();
        //참가 인원에 대한 이름 설정 및 초기 게임머니 설정
        Player[] player = new Player[playernum];
        String playername = "";
        for (int i = 1; i <= playernum; i++) {
            System.out.printf("플레이어%d의 닉네임을 설정하세요.(닉네임: 최대 20글자)> ", i);
            scanner = new Scanner(System.in);
            playername = scanner.nextLine();

            while (playername.length() > 20) {
                System.out.print("닉네임이 너무 깁니다. 재설정 해주세요.(닉네임: 최대 20글자)> ");
                scanner = new Scanner(System.in);
                playername = scanner.nextLine();
            }

            player[i - 1] = new Player(playername);
        }
        for (int i = 0; i < playernum; i++) {
            System.out.printf("플레이어%d 닉네임: %s / 게임머니: %d원%n", i + 1, player[i].nickname, player[i].money);
        }
        System.out.println();
        //게임시작
        System.out.println("게임을 시작합니다.");
        //카드덱 한벌을 가지고 있는 딜러 생성
        System.out.println();
        Dealer d = new Dealer();
        //딜러가 플레이어에게 서로 다른 5장의 카드를 나눠준다.
        for (int i = 0; i < playernum; i++) {
            System.out.printf("%s의 덱: %n", player[i].nickname);
            for (int j = 0; j < player[i].playerdeck.length; j++) {
                player[i].playerdeck[j] = d.pick();
                System.out.println(player[i].playerdeck[j]);
            }
            System.out.println();
        }
        //딜러는 플레이어의 카드를 평가하고 결과를 점수로 반환한다.(점수가 높을 수록 좋음)
        //카드의 평가는 일반적인 포커의 랭크를 참고하여 높은 랭크에게 더 높은 점수를 준다.
        int[] score = new int[playernum];
        int higherscore = 0;
        int winner = -1; // 위너의 디폴트 값은 -1, 플레이어[i]가 0부터 시작되고, 승자의 번호를 winner에 저장하므로 무승부일때는 기본값인 -1이다.
        boolean isDraw = false;

        for (int i = 0; i < playernum; i++) {
            score[i] = d.calculate(player[i]);
            System.out.printf("%s의 점수: %d%n", player[i].nickname, score[i]);
            if (score[i] > higherscore) {
                higherscore = score[i];
                winner = i;
                isDraw = false;
            } else if (score[i] == higherscore) {
                isDraw = true;
            }
        }
        //승무패 조건식
        if (isDraw) {
            System.out.println("승자: 무승부로 승자 없음");
        } else {
            player[winner].wincount++;
            player[winner].money += 100;
            System.out.printf("승자: %s%n", player[winner].nickname);

            for (int i = 0; i < playernum; i++) {
                if (i != winner) {
                    player[i].losecount++;
                }
            }
        }

        //매 게임마다 딜러는 각 플레이어의 카드를 평가하여 결과를 출력한다.
        //게임에서 승리한 플레이어는 상금 100원과 1승이 추가되고 나머지 플레이어는 상금 0원과 1패가 추가된다.
        //100번의 게임을 자동적으로 반복해서 실행하여 최종 결과를 승리의 수가 많은 플레이어부터 내림차순으로 정렬하여 화면에 출력한다.
    }
}

class Dealer {
    final int CARD_NUM = 52;
    Card[] cardArr = new Card[CARD_NUM];
    int cardindex = 0;


    Dealer() {
        int i = 0;

        for (int k = Card.KIND_MAX; k > 0; k--) {
            for (int n = 0; n < Card.NUM_MAX; n++) {
                cardArr[i++] = new Card(k, n + 1);
            }
        }

        shuffledeck();
    }

    Card pick() {
        return cardArr[cardindex++];
    }

    void shuffledeck() {
        for (int i = 0; i < cardArr.length; i++) {
            int r = (int) (Math.random() * CARD_NUM);

            Card tmp = cardArr[i];
            cardArr[i] = cardArr[r];
            cardArr[r] = tmp;
        }
    }

    int calculate(Player players) {
        final int i = 0;
        int[] sortingdeck = {
                players.playerdeck[i].number,
                players.playerdeck[i + 1].number,
                players.playerdeck[i + 2].number,
                players.playerdeck[i + 3].number,
                players.playerdeck[i + 4].number};
        Arrays.sort(sortingdeck);

        boolean samekind = players.playerdeck[i].kind == players.playerdeck[i + 1].kind
                && players.playerdeck[i].kind == players.playerdeck[i + 2].kind
                && players.playerdeck[i].kind == players.playerdeck[i + 3].kind
                && players.playerdeck[i].kind == players.playerdeck[i + 4].kind;

        //로얄 스트레이트 플러시=13점: 카드 5장이 모두 무늬가 같고, 10,J,Q,K,A로 이루어진 패
        if (samekind
                && sortingdeck[i] == 1
                && sortingdeck[i + 1] == 10
                && sortingdeck[i + 2] == 11
                && sortingdeck[i + 3] == 12
                && sortingdeck[i + 4] == 13) {
            return 13;
        }
        //백스트레이트 플러시=12점: 카드 5장이 모두 무늬가 같고, A,2,3,4,5로 이루어진 패 
        else if (samekind
                && sortingdeck[i] == 1
                && sortingdeck[i + 1] == 2
                && sortingdeck[i + 2] == 3
                && sortingdeck[i + 3] == 4
                && sortingdeck[i + 4] == 5) {
            return 12;
        }
        //스트레이트 플러시=11점: 카드 5장이 모두 무늬가 같고, 숫자가 연속적으로 이루어진 패
        else if (samekind
                && ((sortingdeck[i + 1] - sortingdeck[i] == 1) && (sortingdeck[i] != 1))
                && (sortingdeck[i + 2] - sortingdeck[i + 1] == 1)
                && (sortingdeck[i + 3] - sortingdeck[i + 2] == 1)
                && (sortingdeck[i + 4] - sortingdeck[i + 3] == 1)) {
            return 11;
        }
        //포카드=10점: 같은 숫자의 카드 4장으로 이루어진 패
        else if ((sortingdeck[i] == sortingdeck[i + 1]
                && sortingdeck[i] == sortingdeck[i + 2]
                && sortingdeck[i] == sortingdeck[i + 3])
                ||
                (sortingdeck[i + 1] == sortingdeck[i + 2]
                && sortingdeck[i + 1] == sortingdeck[i + 3]
                && sortingdeck[i + 1] == sortingdeck[i + 4])) {
            return 10;
        }
        //풀하우스=9점: 같은 숫자 3개(트리플)와 같은 숫자 2개(원페어)로 이루어진 패
        else if ((sortingdeck[i] == sortingdeck[i + 1]
                && sortingdeck[i] == sortingdeck[i + 2]
                && sortingdeck[i + 3] == sortingdeck[i + 4])
                ||
                (sortingdeck[i] == sortingdeck[i + 1]
                && sortingdeck[i + 2] == sortingdeck[i + 3]
                && sortingdeck[i + 2] == sortingdeck[i + 4])) {
            return 9;
        }
        //플러시=8점: 카드 5장이 모두 무늬가 같은 패
        else if (samekind) {
            return 8;
        }
        //마운틴=7점: 모든 무늬가 같지는 않고, 10,J,Q,K,A로 이루어진 패
        else if (sortingdeck[i] == 1
                && sortingdeck[i + 1] == 10
                && sortingdeck[i + 2] == 11
                && sortingdeck[i + 3] == 12
                && sortingdeck[i + 4] == 13) {
            return 7;
        }
        //백스트레이트=6점: 모든 무늬가 같지는 않고, A,2,3,4,5로 이루어진 패
        else if (sortingdeck[i] == 1
                && sortingdeck[i + 1] == 2
                && sortingdeck[i + 2] == 3
                && sortingdeck[i + 3] == 4
                && sortingdeck[i + 4] == 5) {
            return 6;
        }
        //스트레이트=5점: 모든 무늬가 같지는 않고, 숫자가 연속적으로 이루어진 패
        else if ((sortingdeck[i + 1] - sortingdeck[i] == 1)
                && (sortingdeck[i + 2] - sortingdeck[i + 1] == 1)
                && (sortingdeck[i + 3] - sortingdeck[i + 2] == 1)
                && (sortingdeck[i + 4] - sortingdeck[i + 3] == 1)) {
            return 5;
        }
        //트리플=4점: 5장의 카드 중에서 3장의 숫자가 같은 패
        else if ((sortingdeck[i] == sortingdeck[i + 1]
                && sortingdeck[i] == sortingdeck[i + 2])
                ||
                (sortingdeck[i + 1] == sortingdeck[i + 2]
                && sortingdeck[i + 1] == sortingdeck[i + 3])
                ||
                (sortingdeck[i + 2] == sortingdeck[i + 3]
                && sortingdeck[i + 2] == sortingdeck[i + 4])) {
            return 4;
        }
        //투페어=3점: 같은 숫자 두개(원페어)가 두 쌍이 있는 패
        else if ((sortingdeck[i] == sortingdeck[i + 1]
                && sortingdeck[i + 2] == sortingdeck[i + 3])
                ||
                (sortingdeck[i] == sortingdeck[i + 1]
                && sortingdeck[i + 3] == sortingdeck[i + 4])
                ||
                (sortingdeck[i + 1] == sortingdeck[i + 2]
                && sortingdeck[i + 3] == sortingdeck[i + 4])) {
            return 3;
        }
        //원페어=2점: 같은 숫자 두개가 한 쌍이 있는 패
        else if ((sortingdeck[i] == sortingdeck[i + 1])
                ||
                (sortingdeck[i + 1] == sortingdeck[i + 2])
                ||
                (sortingdeck[i + 2] == sortingdeck[i + 3])
                ||
                (sortingdeck[i + 3] == sortingdeck[i + 4])) {
            return 2;
        }
        //노페어=1점: 어떤 경우에도 해당하지 않는 패
        else {
            return 1;
        }
    }
}

class Card {
    static final int KIND_MAX = 4;
    static final int NUM_MAX = 13;

    static final int SPADE = 4;
    static final int DIAMOND = 3;
    static final int HEART = 2;
    static final int CLOVER = 1;
    int kind;
    int number;

    Card() {
        this(SPADE, 1);
    }

    Card(int kind, int number) {
        this.kind = kind;
        this.number = number;
    }

    public String toString() {
        String[] kinds = {"", "CLOVER", "HEART", "DIAMOND", "SPADE"};
        String numbers = "0123456789XJQK";
        return kinds[this.kind] + ": " + numbers.charAt(this.number);
    }
}

class Player {
    String nickname;
    int money = 10000;
    int wincount;
    int losecount;
    Card[] playerdeck = new Card[5];

    Player(String nickname) {
        this.nickname = nickname;
    }
}