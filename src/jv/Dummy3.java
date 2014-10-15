package jv;

/**
 * Created by miroslav on 13-10-14.
 * java -cp "$HOME/misc/*:$HOME/jarlib/*" jv.Dummy3
 */
public class Dummy3 {

    public static void main(String[] args){
        System.out.println("test switch...");


        for (int nr = 0; nr < 10; nr++) {

            System.out.print(nr + " -> ");

            switch(nr)
            {
                case 0:
                    System.out.println("0");
                    break;
                case 1:
                    System.out.println("1");
                    break;
                case 2:
                case 3:
                    System.out.println("3");
                    break;
                default:
            }


        }





    }

}
