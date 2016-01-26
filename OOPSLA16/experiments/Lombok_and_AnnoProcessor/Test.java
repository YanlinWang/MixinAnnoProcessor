import lombok.Obj;

import com.zewei.annotation.processor.Algebra;

@Algebra@Obj
interface Test {
	int x();
}

// @Obj: generate the of() method
// @Algebra: print out all the method names
// log1.txt: @Obj@Algebra
// log2.txt: @Algebra@Obj
