.method mul_
.args 3
.locals 2
bipush 1
istore 4
bipush 0
iload 1
isub
iflt mul0
bipush 0
iload 1 
isub
istore 1
bipush 0
istore 4
mul0:
bipush 0
istore 3
mul1:
iload 1
ifeq mul2
iload 1
bipush 1
isub
istore 1
iload 3
iload 2
iadd
istore 3
goto mul1
mul2:
bipush 1
iload 4
isub 
ifeq mul3
bipush 0
iload 3
isub
istore 3
mul3:
iload 3
ireturn

.method div_
.args 3
.locals 2
bipush 1
istore 4
bipush 0
iload 1
isub
iflt div0
bipush 0
iload 1 
isub
istore 1
bipush 0
istore 4
div0:
bipush 0
iload 2
isub
iflt div1
bipush 0
iload 2 
isub
istore 2
iload 4
bipush 1
iadd
istore 4
div1:
bipush 0
istore 3
div2:
iload 1
iload 2
isub
iflt div3
iload 1
iload 2
isub
istore 1
iload 3
bipush 1
iadd
istore 3
goto div2
div3:
bipush 0
iload 4
isub 
iflt div4
bipush 0
iload 3
isub
istore 3
div4:
iload 3
ireturn

.method mod_
.args 3
.locals 1
bipush 44
iload 1
iload 2
invokevirtual div_
istore 3
iload 1
bipush 44
iload 3
iload 2
invokevirtual mul_
isub
ireturn

.method not_
.args 2
iload 1
ifeq not1
bipush 0
goto not0
not1:
bipush 1
not0:

.method eq_
.args 3
iload 1
iload 2
isub
ifeq eq1
bipush 0
ireturn
eq1:
bipush 1
ireturn

.method ne_
.args 3
iload 1
iload 2
isub
ifeq ne0
bipush 1
ireturn
ne0:
bipush 0
ireturn

.method lt_
.args 3
iload 1
iload 2
isub
iflt lt1
bipush 0
ireturn
lt1:
bipush 1
ireturn

.method gt_
.args 3
iload 1
iload 2
isub
iflt gt0
bipush 1
ireturn
gt0:
bipush 0
ireturn

.method leq_
.args 3
iload 1
iload 2
isub
dup
ifeq leqpop1
iflt leq1
bipush 0
ireturn
leq1:
bipush 1
ireturn
leqpop1:
pop
bipush 1
ireturn

.method geq_
.args 3
iload 1
iload 2
isub
dup
ifeq geqpop1
iflt geq0
bipush 1
ireturn
geq0:
bipush 0
ireturn
geqpop1:
pop
bipush 1
ireturn

.method main
.args 1
bipush 44
ldc_w 97
invokevirtual putchar
bipush 0
ireturn
