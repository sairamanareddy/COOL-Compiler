	.text
	.file	"helloworld.ll"
	.globl	_ZN6Object8__cons__     # -- Begin function _ZN6Object8__cons__
	.p2align	4, 0x90
	.type	_ZN6Object8__cons__,@function
_ZN6Object8__cons__:                    # @_ZN6Object8__cons__
	.cfi_startproc
# %bb.0:                                # %entry
	xorl	%eax, %eax
	retq
.Lfunc_end0:
	.size	_ZN6Object8__cons__, .Lfunc_end0-_ZN6Object8__cons__
	.cfi_endproc
                                        # -- End function
	.globl	_ZN6Object5abort        # -- Begin function _ZN6Object5abort
	.p2align	4, 0x90
	.type	_ZN6Object5abort,@function
_ZN6Object5abort:                       # @_ZN6Object5abort
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	movl	$1, %edi
	callq	exit
	xorl	%eax, %eax
	popq	%rcx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end1:
	.size	_ZN6Object5abort, .Lfunc_end1-_ZN6Object5abort
	.cfi_endproc
                                        # -- End function
	.globl	_ZN6Object9type_name    # -- Begin function _ZN6Object9type_name
	.p2align	4, 0x90
	.type	_ZN6Object9type_name,@function
_ZN6Object9type_name:                   # @_ZN6Object9type_name
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	movq	8(%rdi), %rdi
	callq	_ZN6String4copy
	popq	%rcx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end2:
	.size	_ZN6Object9type_name, .Lfunc_end2-_ZN6Object9type_name
	.cfi_endproc
                                        # -- End function
	.globl	_ZN2IO8__cons__         # -- Begin function _ZN2IO8__cons__
	.p2align	4, 0x90
	.type	_ZN2IO8__cons__,@function
_ZN2IO8__cons__:                        # @_ZN2IO8__cons__
	.cfi_startproc
# %bb.0:                                # %entry
	xorl	%eax, %eax
	retq
.Lfunc_end3:
	.size	_ZN2IO8__cons__, .Lfunc_end3-_ZN2IO8__cons__
	.cfi_endproc
                                        # -- End function
	.globl	_ZN2IO10out_string      # -- Begin function _ZN2IO10out_string
	.p2align	4, 0x90
	.type	_ZN2IO10out_string,@function
_ZN2IO10out_string:                     # @_ZN2IO10out_string
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movq	%rdi, %rbx
	movl	$.Lstrformatstr, %edi
	xorl	%eax, %eax
	callq	printf
	movq	%rbx, %rax
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end4:
	.size	_ZN2IO10out_string, .Lfunc_end4-_ZN2IO10out_string
	.cfi_endproc
                                        # -- End function
	.globl	_ZN2IO7out_int          # -- Begin function _ZN2IO7out_int
	.p2align	4, 0x90
	.type	_ZN2IO7out_int,@function
_ZN2IO7out_int:                         # @_ZN2IO7out_int
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movq	%rdi, %rbx
	movl	$.Lintformatstr, %edi
	xorl	%eax, %eax
	callq	printf
	movq	%rbx, %rax
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end5:
	.size	_ZN2IO7out_int, .Lfunc_end5-_ZN2IO7out_int
	.cfi_endproc
                                        # -- End function
	.globl	_ZN2IO9in_string        # -- Begin function _ZN2IO9in_string
	.p2align	4, 0x90
	.type	_ZN2IO9in_string,@function
_ZN2IO9in_string:                       # @_ZN2IO9in_string
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movl	$1024, %edi             # imm = 0x400
	callq	malloc
	movq	%rax, %rbx
	movl	$.Lstrformatstr, %edi
	movq	%rax, %rsi
	xorl	%eax, %eax
	callq	scanf
	movq	%rbx, %rax
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end6:
	.size	_ZN2IO9in_string, .Lfunc_end6-_ZN2IO9in_string
	.cfi_endproc
                                        # -- End function
	.globl	_ZN2IO6in_int           # -- Begin function _ZN2IO6in_int
	.p2align	4, 0x90
	.type	_ZN2IO6in_int,@function
_ZN2IO6in_int:                          # @_ZN2IO6in_int
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	.cfi_offset %rbx, -16
	movl	$4, %edi
	callq	malloc
	movq	%rax, %rbx
	movl	$.Lintformatstr, %edi
	movq	%rax, %rsi
	xorl	%eax, %eax
	callq	scanf
	movl	(%rbx), %eax
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end7:
	.size	_ZN2IO6in_int, .Lfunc_end7-_ZN2IO6in_int
	.cfi_endproc
                                        # -- End function
	.globl	_ZN4Main8__cons__       # -- Begin function _ZN4Main8__cons__
	.p2align	4, 0x90
	.type	_ZN4Main8__cons__,@function
_ZN4Main8__cons__:                      # @_ZN4Main8__cons__
	.cfi_startproc
# %bb.0:                                # %entry
	movl	$3, 16(%rdi)
	movl	$20, (%rdi)
	movq	$.L.str0, 8(%rdi)
	xorl	%eax, %eax
	retq
.Lfunc_end8:
	.size	_ZN4Main8__cons__, .Lfunc_end8-_ZN4Main8__cons__
	.cfi_endproc
                                        # -- End function
	.globl	_ZN4Main4main           # -- Begin function _ZN4Main4main
	.p2align	4, 0x90
	.type	_ZN4Main4main,@function
_ZN4Main4main:                          # @_ZN4Main4main
	.cfi_startproc
# %bb.0:                                # %entry
	movl	$3, %eax
	retq
.Lfunc_end9:
	.size	_ZN4Main4main, .Lfunc_end9-_ZN4Main4main
	.cfi_endproc
                                        # -- End function
	.globl	main                    # -- Begin function main
	.p2align	4, 0x90
	.type	main,@function
main:                                   # @main
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbx
	.cfi_def_cfa_offset 16
	subq	$32, %rsp
	.cfi_def_cfa_offset 48
	.cfi_offset %rbx, -16
	leaq	8(%rsp), %rbx
	movq	%rbx, %rdi
	callq	_ZN4Main8__cons__
	movq	%rbx, %rdi
	callq	_ZN4Main4main
	xorl	%eax, %eax
	addq	$32, %rsp
	.cfi_def_cfa_offset 16
	popq	%rbx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end10:
	.size	main, .Lfunc_end10-main
	.cfi_endproc
                                        # -- End function
	.globl	_ZN6String6length       # -- Begin function _ZN6String6length
	.p2align	4, 0x90
	.type	_ZN6String6length,@function
_ZN6String6length:                      # @_ZN6String6length
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rax
	.cfi_def_cfa_offset 16
	callq	strlen
                                        # kill: def $eax killed $eax killed $rax
	popq	%rcx
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end11:
	.size	_ZN6String6length, .Lfunc_end11-_ZN6String6length
	.cfi_endproc
                                        # -- End function
	.globl	_ZN6String6concat       # -- Begin function _ZN6String6concat
	.p2align	4, 0x90
	.type	_ZN6String6concat,@function
_ZN6String6concat:                      # @_ZN6String6concat
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%r14
	.cfi_def_cfa_offset 16
	pushq	%rbx
	.cfi_def_cfa_offset 24
	pushq	%rax
	.cfi_def_cfa_offset 32
	.cfi_offset %rbx, -24
	.cfi_offset %r14, -16
	movq	%rsi, %r14
	callq	_ZN6String4copy
	movq	%rax, %rbx
	movq	%rax, %rdi
	movq	%r14, %rsi
	callq	strcat
	movq	%rbx, %rax
	addq	$8, %rsp
	.cfi_def_cfa_offset 24
	popq	%rbx
	.cfi_def_cfa_offset 16
	popq	%r14
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end12:
	.size	_ZN6String6concat, .Lfunc_end12-_ZN6String6concat
	.cfi_endproc
                                        # -- End function
	.globl	_ZN6String4copy         # -- Begin function _ZN6String4copy
	.p2align	4, 0x90
	.type	_ZN6String4copy,@function
_ZN6String4copy:                        # @_ZN6String4copy
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%r14
	.cfi_def_cfa_offset 16
	pushq	%rbx
	.cfi_def_cfa_offset 24
	pushq	%rax
	.cfi_def_cfa_offset 32
	.cfi_offset %rbx, -24
	.cfi_offset %r14, -16
	movq	%rdi, %r14
	movl	$1024, %edi             # imm = 0x400
	callq	malloc
	movq	%rax, %rbx
	movq	%rax, %rdi
	movq	%r14, %rsi
	callq	strcpy
	movq	%rbx, %rax
	addq	$8, %rsp
	.cfi_def_cfa_offset 24
	popq	%rbx
	.cfi_def_cfa_offset 16
	popq	%r14
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end13:
	.size	_ZN6String4copy, .Lfunc_end13-_ZN6String4copy
	.cfi_endproc
                                        # -- End function
	.globl	_ZN6String6substr       # -- Begin function _ZN6String6substr
	.p2align	4, 0x90
	.type	_ZN6String6substr,@function
_ZN6String6substr:                      # @_ZN6String6substr
	.cfi_startproc
# %bb.0:                                # %entry
	pushq	%rbp
	.cfi_def_cfa_offset 16
	pushq	%r14
	.cfi_def_cfa_offset 24
	pushq	%rbx
	.cfi_def_cfa_offset 32
	.cfi_offset %rbx, -32
	.cfi_offset %r14, -24
	.cfi_offset %rbp, -16
	movl	%edx, %r14d
	movslq	%esi, %rbx
	addq	%rdi, %rbx
	movl	$1024, %edi             # imm = 0x400
	callq	malloc
	movq	%rax, %rbp
	movq	%rax, %rdi
	movq	%rbx, %rsi
	movl	%r14d, %edx
	callq	strncpy
	movslq	%r14d, %rax
	movb	$0, (%rbp,%rax)
	movq	%rbp, %rax
	popq	%rbx
	.cfi_def_cfa_offset 24
	popq	%r14
	.cfi_def_cfa_offset 16
	popq	%rbp
	.cfi_def_cfa_offset 8
	retq
.Lfunc_end14:
	.size	_ZN6String6substr, .Lfunc_end14-_ZN6String6substr
	.cfi_endproc
                                        # -- End function
	.type	.LAbortdivby0,@object   # @Abortdivby0
	.section	.rodata.str1.1,"aMS",@progbits,1
.LAbortdivby0:
	.asciz	"Error: Division by 0\n"
	.size	.LAbortdivby0, 22

	.type	.LAbortdispvoid,@object # @Abortdispvoid
.LAbortdispvoid:
	.asciz	"Error: Dispatch to void\n"
	.size	.LAbortdispvoid, 25

	.type	.Lstrformatstr,@object  # @strformatstr
.Lstrformatstr:
	.asciz	"%s"
	.size	.Lstrformatstr, 3

	.type	.Linstrformatstr,@object # @instrformatstr
.Linstrformatstr:
	.asciz	"%[^\n]s"
	.size	.Linstrformatstr, 7

	.type	.Lintformatstr,@object  # @intformatstr
.Lintformatstr:
	.asciz	"%d"
	.size	.Lintformatstr, 3

	.type	.L.str0,@object         # @.str0
.L.str0:
	.asciz	"Main"
	.size	.L.str0, 5

	.section	".note.GNU-stack","",@progbits
