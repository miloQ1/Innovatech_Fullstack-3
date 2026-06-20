import { IonApp, setupIonicReact } from '@ionic/react';
import { AuthProvider } from './context/AuthContext';
import { AppRouter } from './routes/AppRouter';

setupIonicReact();

function App() {
  return (
    <IonApp>
      <AuthProvider>
        <AppRouter />
      </AuthProvider>
    </IonApp>
  );
}

export default App;
