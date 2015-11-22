package pegasusLombok;

import lombok.Mixin;


interface Animal {}
@Mixin interface Horse {
	default void run() {System.out.println("I can run!");};
}
//BEGIN_PEGASUS_LOMBOK
@Mixin interface Bird {
	default void fly() {System.out.println("I can fly!");}
}
@Mixin interface Pegasus extends Horse, Bird {}
//END_PEGASUS_LOMBOK

public interface Main {
	public static void main(String[] args) {
		Pegasus p = Pegasus.of();
	}

}