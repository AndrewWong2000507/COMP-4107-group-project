package ATMSS.BuzzerHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;

public class BuzzerHandler extends HWHandler {
    private String id;
    private AppKickstarter appKickstarter;

    public BuzzerHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        this.id = id;
        this.appKickstarter = appKickstarter;
    }

    @Override
    protected void processMsg(Msg msg) {
        switch (msg.getType()) {
            case Buzzer_ON:
                atmss.send(new Msg(id, mbox, Msg.Type.Buzzer_ON, msg.getDetails()));
                buzzerOn();
                break;
            case Buzzer_OFF:
                atmss.send(new Msg(id, mbox, Msg.Type.Buzzer_OFF, msg.getDetails()));
                buzzerOff();
                break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    }

    protected void buzzerOn() {
        log.info(id + ": buzzer is on");
    }

    protected void buzzerOff() {
        log.info(id + ": buzzer is off");
    }
}
