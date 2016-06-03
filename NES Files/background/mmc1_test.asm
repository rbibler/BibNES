  .inesprg 8   ; 1x 16KB PRG code
  .ineschr 1   ; 1x  8KB CHR data
  .inesmap 1   ; mapper 0 = NROM, no bank swapping
  .inesmir %10   ; background mirroring
  


currentX 	.rs 1
currentY 	.rs 1
currentBank .rs 1
swapCount   .rs 1
    
  .bank 0
  .incbin "bank0.txt"

  .bank 1
  .incbin "bank1.txt"
  
  .bank 2
  .incbin "bank2.txt"
  
  .bank 3
  .incbin "bank3.txt"
  
  .bank 4
  .incbin "bank4.txt"
  
  .bank 5
  .incbin "bank5.txt"
  
  .bank 6
  .incbin "bank6.txt"

  .bank 7
  .incbin "bank7.txt"
  
  .bank 8
  .incbin "bank8.txt"
  
  .bank 9
  .incbin "bank9.txt"
  
  .bank 10
  .incbin "bank10.txt"
  
  .bank 11
  .incbin "bank11.txt"
  
  .bank 12
  .incbin "bank10.txt"
  
  .bank 13
  .incbin "bank11.txt"
  
  .bank 14
palette:
  .db $21,$24,$27,$26,  $21,$2C,$2D,$3D,  $21,$2C,$31,$3D,  $21,$37,$17,$18   ;;background palette
  .db $21,$07,$06,$27,  $21,$07,$11,$27,  $21,$07,$15,$27,  $21,$07,$28,$2D   ;;sprite palette
  
  .bank 15
  .org $E000
  
  
SetBankConfig:
  LDA #$80
  STA $8000
  
  LDA #$0E
  STA $8000
  LSR A
  STA $8000
  LSR A
  STA $8000
  LSR A
  STA $8000
  LSR A
  STA $8000
  RTS
  
PRGBankWrite:
  LDA currentBank
  STA $E000
  LSR A
  STA $E000
  LSR A
  STA $E000
  LSR A
  STA $E000
  LSR A
  STA $E000
  RTS



DisplayLine:
  LDA $2002
  LDA #$21
  STA $2006
  LDA #$40
  STA $2006
  LDX #$00
ReadLine:
  LDA $8000, x
  CMP #$D
  BEQ Done
  STA $2007
  INX
  JMP ReadLine
Done:
  RTS
  
RESET:
  SEI          ; disable IRQs
  CLD          ; disable decimal mode
  LDX #$40
  STX $4017    ; disable APU frame IRQ
  LDX #$FF
  TXS          ; Set up stack
  INX          ; now X = 0
  STX $2000    ; disable NMI
  STX $2001    ; disable rendering
  STX $4010    ; disable DMC IRQs

vblankwait1:       ; First wait for vblank to make sure PPU is ready
  BIT $2002
  BPL vblankwait1

clrmem:
  LDA #$00
  STA $0000, x
  STA $0100, x
  STA $0300, x
  STA $0400, x
  STA $0500, x
  STA $0600, x
  STA $0700, x
  LDA #$FE
  STA $0200, x
  INX
  BNE clrmem
   
vblankwait2:      ; Second wait for vblank, PPU is ready after this
  BIT $2002
  BPL vblankwait2
  
LoadPalettes:
  LDA $2002             ; read PPU status to reset the high/low latch
  LDA #$3F
  STA $2006             ; write the high byte of $3F00 address
  LDA #$00
  STA $2006             ; write the low byte of $3F00 address
  LDX #$00              ; start out at 0
LoadPalettesLoop:
  LDA palette, x        ; load data from address (palette + the value in x)
                          ; 1st time through loop it will load palette+0
                          ; 2nd time through loop it will load palette+1
                          ; 3rd time through loop it will load palette+2
                          ; etc
  STA $2007             ; write to PPU
  INX                   ; X = X + 1
  CPX #$20              ; Compare X to hex $10, decimal 16 - copying 16 bytes = 4 sprites
  BNE LoadPalettesLoop  ; Branch to LoadPalettesLoop if compare was Not Equal to zero
                        ; if compare was equal to 32, keep going down
  
  LDA #%10010000   ; enable NMI, sprites from Pattern Table 0, background from Pattern Table 1
  STA $2000

  LDA #%00011110   ; enable sprites, enable background, no clipping on left side
  STA $2001

  
  JSR SetBankConfig
  LDA #$00
  STA currentBank
  JSR PRGBankWrite
  

  

Forever:
  JMP Forever     ;jump back to Forever, infinite loop
  
 

NMI:


  LDA #$00
  STA $2003       ; set the low byte (00) of the RAM address
  LDA #$02
  STA $4014       ; set the high byte (02) of the RAM address, start the transfer
  
  JSR DisplayLine
  LDA swapCount
  CLC
  ADC #$01
  STA swapCount
  CMP #$FF
  BNE DONE
  LDA currentBank
  CLC
  ADC #$01
  CMP #$A
  BNE NoResetBankCounter
  LDA #$00
NoResetBankCounter:
  STA currentBank
  JSR PRGBankWrite
DONE:
  



  ;;This is the PPU clean up section, so rendering the next frame starts properly.
  LDA #%10010000   ; enable NMI, sprites from Pattern Table 0, background from Pattern Table 1
  STA $2000
  LDA #%00011110   ; enable sprites, enable background, no clipping on left side
  STA $2001
  LDA #$00        ;;tell the ppu there is no background scrolling
  STA $2005
  STA $2005
  
  RTI             ; return from interrupt
  

  .org $FFFA     ;first of the three vectors starts here
  .dw NMI        ;when an NMI happens (once per frame if enabled) the 
                   ;processor will jump to the label NMI:
  .dw RESET      ;when the processor first turns on or is reset, it will jump
                   ;to the label RESET:
  .dw 0          ;external interrupt IRQ is not used in this tutorial
  
  
;;;;;;;;;;;;;;  
  .bank 16
  .org $0000
  .incbin "graphics.bin"
  
  