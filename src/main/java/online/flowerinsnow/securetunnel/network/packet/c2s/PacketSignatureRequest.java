package online.flowerinsnow.securetunnel.network.packet.c2s;

import online.flowerinsnow.securetunnel.network.packet.PacketBase;
import online.flowerinsnow.securetunnel.network.packet.PacketInfo;
import online.flowerinsnow.securetunnel.network.packet.PacketSide;

/**
 * <p></p>
 */
@PacketInfo(id = 0x05, side = PacketSide.CLIENT_TO_SERVER)
public class PacketSignatureRequest extends PacketBase {
    private byte[] signatureID;

    public PacketSignatureRequest() {
    }

    public PacketSignatureRequest(byte[] signatureID) {
        this.signatureID = signatureID;
    }
}
