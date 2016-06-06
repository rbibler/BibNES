  .org $40
  
  LDA #$44
  STA $0200
  LDX $FF
LOOP:          ;This is where we loop to until the loop is finished
  CLC
  ADC #$01
  STA $0200
  DEX
  CPX #$00
  BNE LOOP
  STA $0201