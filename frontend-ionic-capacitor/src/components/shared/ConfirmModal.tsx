import { IonAlert } from '@ionic/react';

interface ConfirmModalProps {
  title: string;
  message: string;
  confirmLabel?: string;
  cancelLabel?: string;
  danger?: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}

export function ConfirmModal({
  title,
  message,
  confirmLabel = 'Confirmar',
  cancelLabel = 'Cancelar',
  danger = false,
  onConfirm,
  onCancel,
}: ConfirmModalProps) {
  return (
    <IonAlert
      isOpen
      header={title}
      message={message}
      onDidDismiss={onCancel}
      buttons={[
        { text: cancelLabel, role: 'cancel', handler: onCancel },
        { text: confirmLabel, role: danger ? 'destructive' : 'confirm', handler: onConfirm },
      ]}
    />
  );
}
