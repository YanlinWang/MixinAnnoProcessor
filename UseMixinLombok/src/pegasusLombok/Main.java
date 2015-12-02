package pegasusLombok;

import lombok.Obj;


interface Animal {}

//BEGIN_PEGASUS_LOMBOK
@Obj interface Horse extends Animal{
	default void run() {System.out.println("I can run!");};
}
@Obj interface Bird extends Animal{
	default void fly() {System.out.println("I can fly!");}
}
@Obj interface Pegasus extends Horse, Bird {}
//END_PEGASUS_LOMBOK

public interface Main {
	public static void main(String[] args) {
		Pegasus p = Pegasus.of();
	}

}
